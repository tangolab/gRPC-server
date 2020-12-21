#gRPC example - uses spring boot
#Step 1. Specify the server port in application.yml 
#Step 2. Start the server
mvn spring-boot:run

#Step 3. locate the the .proto file in the src/main/proto folder
#Step 4. Use BloomRPC and point it to the .proto file, specify the port that BloomRPC 7should connect to
#Step 5. After the proto file is loaded, double click the method to test
#Step 6. Specify the parameters and click the "play/go" icon