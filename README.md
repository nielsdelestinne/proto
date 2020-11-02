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

### Backward compatibility
- We tagged the repository with tag `1.0.0`, in this version both the provider ('server', employment) as the consumer ('client', recruitment) use 
the proto contract (employment-service-contract) 1.0.0 version.
- We tagged a later version of the repository with tag `2.0.0`, in this version we updated the proto contract of employer by changing the field `name` to `fullName`.
This is released as the employment-service-contract 2.0.0 version. The employment-service (provider) upgraded to this version and had to change the compilation errors (getName() -> getFullName()).
The consumer (recruitment-service) remains on version 1.0.0 of the contract.

> Run `store-in-local-m2-repository.bat` to install the 1.0.0 version (`employment-service-contract/artifacts/employment-service-contract-1.0.0-jar-with-dependencies.jar`) into the local m2 repository.
> This way, it can still be used by the consumer.

Although the change to the proto contract was a rename of a field, it is (as far as protobuf is concerned) a non-breaking change.
- Messages sent from the provider using the v1.0.0 of the contract (`name`) to the provider (v2.0.0, `fullName`) will be accepted and the actual value for `fullName` 
will be persisted. This is due to the fact that the protobuf message is serialized into a binary format using only the field numbers (`2`) not the field names `name` / `fullName`.
- The generated java code will however be breaking (getName()) -> getFullName()), and thus when using SemVer, a rename to a field will be a breaking change (to Java, not Protobuf)

Although now sending a request from the recruitment-service with `name: Harry Potter` and having a server accepting `fullName`, the actual value is correctly persisted in the database.
- Fire up the EmploymentService, launch the request by starting the RecruitmentService, inspect the database of EmploymentService using `http://localhost:8080/h2-console`.

Protobuf is both backward & forward compatible.
- JSON can support forward compatibility by adding @JsonIgnoreProperties(ignoreUnknown = true)
- JSON can support (limited) backward compatibility (v2 can have a field v1 does not have. But v2 can not rename an existing field)

More information on Backward & Forward compatibility:
- https://www.beautifulcode.co/blog/88-backward-and-forward-compatibility-protobuf-versioning-serialization
- https://simplicable.com/new/backward-compatibility-vs-forward-compatibility#:~:text=Backward%20compatibility%20is%20a%20design,the%20same%20data%20and%20equipment.&text=Forward%20compatibility%20is%20a%20design,with%20future%20versions%20of%20itself.


### Questions

Open questions:
- [ ] How about different encodings & compression: GZIP
- [ ] What about size & speed compared with json, can we make a different employment-service-json?
- [ ] What about a consumer module (payroll)?  

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
