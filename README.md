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
