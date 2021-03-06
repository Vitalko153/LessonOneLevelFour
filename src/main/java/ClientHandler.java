import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }


    @Override
    public void run() {
        try(
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                ){
            System.out.printf("Client %s connected\n", socket.getInetAddress());
            while(true){
                String command = in.readUTF();
                if ("upload".equals(command)){
                    try{
                        File file = new File("server" + File.separator + in.readUTF());
                        if(!file.exists()){
                            file.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(file);

                        long size = in.readLong();

                        byte[] buffer = new byte[8 * 1024];

                        for (int i = 0; i < (size + (buffer.length - 1)) / (buffer.length); i++) {
                            int read = in.read(buffer);
                            fos.write(buffer, 0, read);
                        }

                        fos.close();
                        out.writeUTF("Upload complete.");

                    } catch (IOException e) {
                        out.writeUTF("Error upload");
                    }
                }

                if ("download".equals(command)){

                }

                if ("exit".equals(command)){
                    System.out.printf("Client %s disconnected correctly\n", socket.getInetAddress());
                    break;
                }

                System.out.println(command);
                out.writeUTF(command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
