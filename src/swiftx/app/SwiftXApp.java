    package swiftx.app;

    import swiftx.transfer.receiver.FileReceiverServer;
    import swiftx.transfer.sender.FileSender;

    import java.io.File;

    public class SwiftXApp {
        private static final String  VERSION = "1.0.0";

        public static void main(String[] args) throws Exception {



            // Flags and helper methods
            // Help flag
            if(args.length == 0 || args[0].equalsIgnoreCase("--help")||
                    args[0].equalsIgnoreCase("-h")){
                printHelp();
                return;
            }

            // Version FLag
            if(args[0].equalsIgnoreCase("--version")||
                    args[0].equalsIgnoreCase("--v")){
                System.out.println("SwiftX version - " + VERSION);
            }

            // Send and Receive

            if (args[0].equalsIgnoreCase("send")){
                if(args.length != 4){
                    System.out.println("Invalid arguments");
                    printHelp();
                    return;
                }
                String ip  = args[1];
                int port = Integer.parseInt(args[2]);
                File file = new File(args[3]);

                // Checking if File exists
                if(!file.exists()){
                    System.out.println("File does not exist");
                    return;
                }
                FileSender.sendFile(ip, port, file);
                return;



            }

            if (args[0].equalsIgnoreCase("receive")) {

                if (args.length != 2) {
                    System.out.println("Invalid arguments for receive");
                    printHelp();
                    return;
                }

                int port = Integer.parseInt(args[1]);
                FileReceiverServer.start(port);
                return;
            }
        }

        // Method to show help
        private static void printHelp(){
            System.out.println("""
            SwiftX — Fast local file transfer CLI
    
            Usage:
              swiftx send <ip> <port> <file>
              swiftx receive <port>
    
            Options:
              --help, -h       Show this help message
              --version, -v    Show version information
    
            Examples:
              swiftx receive 5000
              swiftx send 127.0.0.1 5000 test.txt
    
            Notes:
              • Receiver must be started before sender
              • Files are read/saved from the current directory"""
            );
        }




    }


