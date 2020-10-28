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
