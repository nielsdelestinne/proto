# Protobuf

## Walkthrough

### Setup
1. Create the employment-service
2. Create the contract up front using the protobuf schema, resulting in `.proto` files. Do this in a separate module, 
employment-service-contract as we want to build & release it (SemVer) and use it by both consumer & provider to generate java classes from.

### Generate `.java` & `.jar` files from `.proto` files
1. Include the protobuf core package (required by the generate java classes) & use the [Protoc Jar Maven Plugin](https://github.com/os72/protoc-jar-maven-plugin).
    - Normally, we have to manually download en run the Protobuf compiler to compile `.proto` files into `.java` and `.class` files. However, this plugin does this all for us (including having access to the compiler without being installed locally).
    - The generated sources `.java` will be placed inside `target/generated-sources` and could be used by the module itself (in this scenario, not required as the module is purely dedicated to the contract itself).
    The generated `.class` files can be found in `target/classes` and they will be contained in the generated `.jar` file on which other modules can depend!

### Use generated files & Setup controller
1. From module employment-service, add a dependency to the employment-service-contract (current version 1.0.0). This way,
we'll have a dependency on the generated jar which contains all the generated files from our `.proto` file.
2. Create a basic spring boot setup, nothing to fancy (web & data starter with h2 in-memory)
3. Create a controller, not a rest controller as we are not striving for a RESTful web service. It's much more RPC.
4. Because it's difficult to send in a byte[] array as the request body, we send in a Base64 encoded string of a byte array.
5. We do return a byte[] array (be it as part of a ResponseEntity, so the toString method is not called on the byte[]).
6. Use the `createEmployer.http` to create an employer (the base64 string is generated on startup: see `EmploymentServiceApplication`).
7. The bytes are returned, you can inspect the h2 console on `http://localhost:8080/h2-console` to see the data is correctly persisted.

### Sharing the proto file, or the generated classes?
We created a separate module (employment-service-contract) that contains the `.proto` files and generates the java 
classes which are packaged inside of the `.jar`. Other modules / projects depending on this module, will have access to these generated sources.
We can create builds and generate artifacts `.jar` for our contract and version them based the SemVer strategy.  
- We could have opted to just version and share the `.proto` files and let the different consuming modules generated the sources.
This would have multiple downsides, such as for one: consuming modules would require the protobuf compiler (maven plugin).

### Backward & Forward Compatibility
- We tagged the repository with tag `1.0.0`, in this version both the provider ('server', employment) as the consumer ('client', recruitment) use 
the proto contract (employment-service-contract) 1.0.0 version.
- We tagged a later version of the repository with tag `2.0.0-backward-compatibility`, in this version we updated the proto contract of employer by changing the field `name` to `employerName`.
This is released as the employment-service-contract 2.0.0 version. The employment-service (provider) upgraded to this version and had to change the compilation errors (getName() -> getFullName()).
The consumer (recruitment-service) remains on version 1.0.0 of the contract.

> Run `store-in-local-m2-repository.bat` to install the 1.0.0 version (`employment-service-contract/artifacts/employment-service-contract-1.0.0-jar-with-dependencies.jar`) into the local m2 repository.
> This way, it can still be used by the consumer.

Although the change to the protocol buffer definition was the addition of 2 fields and(!) a rename of a field, it is (as far as protobuf is concerned) a non-breaking change and fully backward 
compatible with older versions of that protocol buffer defintion.
- Messages sent from the provider using the v1.0.0 of the contract (`name`) to the provider (v2.0.0, `employerName`) will be accepted and the actual value for `employerName` 
will be persisted. This is due to the fact that the protobuf message is serialized into a binary format using only the tag numbers (`2`) not the field names `name` / `employerName`.
- The new fields (`size` & `isFamilyOwned`) part of v2.0.0 are simply not part of the v1.0.0 definition, thus they will not be part of those older message. 
When the server, using v2.0.0 of the definition parses these older message, the missing elements will have their corresponding fields in the parsed object set to the default value for that field, based on its type.
    - E.g. field of type string has default value 'empty', enum has as its default value the enum value tagged with 0 (must be the first value), bools are false,... 
- The generated java code will however be breaking (getName()) -> getFullName()), and thus when using SemVer, a rename to a field will be a breaking change (to Java, not Protobuf)

When sending an older v1.0.0 message to a v2.0.0 expecting server, it will be persisted with default values:
- Sent 
```
id {
  value: "123"
}
name: "Building Corporation Y"
```
- Persisted
```
| ID |	IS_FAMILY_OWNED | EMPLOYER_NAME            | SIZE   |
|123 |	FALSE	        | Building Corporation Y   | SMALL  |
```

Although now sending a request from the recruitment-service with `name: Building Corporation Y` and having a server accepting `employerName`, the actual value is correctly persisted in the database.
- Fire up the EmploymentService, launch the request by starting the RecruitmentService, inspect the database of EmploymentService using `http://localhost:8080/h2-console`.

The above scenario is an example is Protocol buffers being backward compatible
- The old (v1.0.0) definition of the protocol buffer sent by the client is accepted and even 'correctly' processed by the server which is on definition v2.0.0.
- _The input is designed for an older version of the system, but still accepted & processed by a newer version of that same system._

Protobuf is also forward compatible, and can be demonstrated by the following example:
- A newer version (v2.0.0) of the protocol buffer is sent by the client and is accepted and correctly processed by the server that is still expecting v1.0.0.
- _an implementation (server using protocol buffers) that uses an older version of the message processes a future version of the message._
- (checkout git tag 2.0.0-forward-compatibility)
- Sent (v2.0.0)
```
id {
  value: "123"
}
employerName: "Building Corporation Y"
size: MEDIUM
isFamilyOwned: true
```
- Persisted (v1.0.0)
```
| ID    |  	NAME                    |  
| 123   |	Building Corporation Y  |
```

So, how about JSON?
- JSON allows for backward compatibility (but its more up to the developer)
    - Renames need to be performed using deprecation and allowing clients / servers to migrate before removing the deprecation
    - Older json messages (missing certain properties that are added in a newer version) should be processed without issue
    - With complex changes or a combination of changes, it might be best to start versioning the json contracts. 
- JSON can support forward compatibility by adding @JsonIgnoreProperties(ignoreUnknown = true)

More information on Backward & Forward compatibility:
- https://www.beautifulcode.co/blog/88-backward-and-forward-compatibility-protobuf-versioning-serialization
- https://simplicable.com/new/backward-compatibility-vs-forward-compatibility#:~:text=Backward%20compatibility%20is%20a%20design,the%20same%20data%20and%20equipment.&text=Forward%20compatibility%20is%20a%20design,with%20future%20versions%20of%20itself.
- https://en.wikipedia.org/wiki/Backward_compatibility
- https://en.wikipedia.org/wiki/Forward_compatibility

### Size comparison
- For the same amount of data (a CreateEmployerRequest) the JSON (using UTF-8 encoding) measures 125 bytes, 
the proto encoded object counts 35 bytes.

### GZIP compression
- Using GZIP, **it highly depends on the data** if it will have a positive impact on the encoded proto object.
- When there is a lot of repeated data in strings,... it will have a positive impact on the size.
However, in other scenarios such as is the case in `ComparisonApplication`, the size actually increased from 35 -> 55 bytes
- JSON went down from 125 to 123 bytes after compression  

### Deserialization performance
Measured using JMH: see `DeserializationComparisonApplication` & `x-results.txt` files.
- http://tutorials.jenkov.com/java-performance/jmh.html

### Serialization performance
Measured using JMH: see `SerializationComparisonApplication` & `x-results.txt` files.
- JsonPrinter has bad performance, but should only be used for manual testing needs. Not PRD code. 

### Questions

Open questions:
- [ ] What about size & speed compared with json, can we make a different employment-service-json?

### Extending a Protocol buffer

> https://developers.google.com/protocol-buffers/docs/javatutorial#extending-a-protocol-buffer

Sooner or later after you release the code that uses your protocol buffer, you will undoubtedly want 
to "improve" the protocol buffer's definition. If you want your new buffers to be backwards-compatible, 
and your old buffers to be forward-compatible – and you almost certainly do want this – then there are 
some rules you need to follow. In the new version of the protocol buffer:

- you must not change the tag numbers of any existing fields.
- (v2 only) you must not add or delete any required fields.
- (v2 only) you may delete optional or repeated fields.
- you may add new optional or repeated fields but you must use fresh tag numbers (i.e. tag numbers that were never used in this protocol buffer, not even by deleted fields).

ALSO read on this subject (better)!: https://developers.google.com/protocol-buffers/docs/proto3#updating

If an existing message type no longer meets all your needs – for example, you'd like the message format to have an extra field – 
but you'd still like to use code created with the old format, don't worry! It's very simple to update message types without breaking any of your existing code. Just remember the following rules:
- Don't change the field numbers for any existing fields.
- If you add new fields, any messages serialized by code using your "old" message format can still be parsed by your new generated code. You should keep in mind the default values for these elements so that new code can properly interact with messages generated by old code. Similarly, messages created by your new code can be parsed by your old code: old binaries simply ignore the new field when parsing. See the Unknown Fields section for details.
- Fields can be removed, as long as the field number is not used again in your updated message type. You may want to rename the field instead, perhaps adding the prefix "OBSOLETE_", or make the field number reserved, so that future users of your .proto can't accidentally reuse the number.
- int32, uint32, int64, uint64, and bool are all compatible – this means you can change a field from one of these types to another without breaking forwards- or backwards-compatibility. If a number is parsed from the wire which doesn't fit in the corresponding type, you will get the same effect as if you had cast the number to that type in C++ (e.g. if a 64-bit number is read as an int32, it will be truncated to 32 bits).
- sint32 and sint64 are compatible with each other but are not compatible with the other integer types.
- string and bytes are compatible as long as the bytes are valid UTF-8.
- Embedded messages are compatible with bytes if the bytes contain an encoded version of the message.
- fixed32 is compatible with sfixed32, and fixed64 with sfixed64.
- For string, bytes, and message fields, optional is compatible with repeated. Given serialized data of a repeated field as input, clients that expect this field to be optional will take the last input value if it's a primitive type field or merge all input elements if it's a message type field. Note that this is not generally safe for numeric types, including bools and enums. Repeated fields of numeric types can be serialized in the packed format, which will not be parsed correctly when an optional field is expected.
- enum is compatible with int32, uint32, int64, and uint64 in terms of wire format (note that values will be truncated if they don't fit). However be aware that client code may treat them differently when the message is deserialized: for example, unrecognized proto3 enum types will be preserved in the message, but how this is represented when the message is deserialized is language-dependent. Int fields always just preserve their value.
- Changing a single value into a member of a new oneof is safe and binary compatible. Moving multiple fields into a new oneof may be safe if you are sure that no code sets more than one at a time. Moving any fields into an existing oneof is not safe.

### Comparing with Avro and others
- https://martin.kleppmann.com/2012/12/05/schema-evolution-in-avro-protocol-buffers-thrift.html
- https://capnproto.org/index.html

### Additional downsides
- A protobuf message needs to be decoded entirely before being accessed (loaded into RAM entirely, even if only portion of message is used)
    - So, protobuf doesn't have a hard size-limitation, but as a rule of thumb 1mb is used (but 10mb might be find to as long as the decoded message fits into the memory)
    - Great answer by one of the creators of Protobuf and Cap'n proto on this issue
        ```
        Does the performance of encoding/decoding suffer horribly at large sizes (around ~10MB)..?
      
        10MB is pushing it but you'll probably be OK.
        
        Protobuf has a hard limit of 2GB, because many implementations use 32-bit signed arithmetic. 
      For security reasons, many implementations (especially the Google-provided ones) impose a size limit 
      of 64MB by default, although you can increase this limit manually if you need to.
        
        The implementation will not "slow down" with large messages per se, but the problem is that you must 
      always parse an entire message at once before you can start using any of the content. 
      This means the entire message must fit into RAM (keeping in mind that after parsing the in-memory message 
      objects are much larger than the original serialized message), and even if you only care about one field 
      you have to wait for the whole thing to parse.
        
        Generally I recommend trying to limit yourself to 1MB as a rule of thumb. Beyond that, think about splitting 
      the message up into multiple chunks that can be parsed independently. However, every application -- for some, 
      10MB is no big deal, for others 1MB is already way too large. You'll have to profile your own app to find out.
        
        I've actually seen cases where people were happy sending messages larger than 1GB, so... it "works".
        
        On a side note, Cap'n Proto has a very similar design to Protobuf but can support messages up to 2^64 bytes 
      (2^32 segments of 4GB each), and it actually does allow you to read one field from the message without parsing 
      the whole message (if it's in a file on disk, use mmap() to avoid reading the whole thing in).
        
        (Disclosure: I'm the author of Cap'n Proto as well as most of Google's open source Protobuf code.)
      
        (other answer)
        I don't think the protobuf compiler will ever complain about message sizes. Atleast not until you get to 
      the 18 exabyte maximum of uint64_t.
      
        For most implementations, performance starts to suffer at the point where the message cannot fit into RAM at once. 
      So 10 MB should be fine, 10 GB not. Another possible issue is if you don't need all of the data - protobuf does not support random access, so you need to decode the whole message even if you only need a part of it.
        ```
    - Cap'n proto offers random access, GSON library supports streaming or even mixed reads (using both streaming and object model reading at the same time): https://sites.google.com/site/gson/streaming  
