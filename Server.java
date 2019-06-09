import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;


public class Server {
 public static void main(String[] args) throws IOException {

  int nsd_port = Integer.parseInt(args[0]);
  InetAddress[] addr;
  ServerSocket ss = null;
  InetAddress addressv4 = null;
  try {
   addr = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
   for (InetAddress address: addr) {
    if (address instanceof Inet6Address) {
     ss = new ServerSocket(nsd_port, 500, address);
    } else {
     addressv4 = address;
    }
   }
  } catch (UnknownHostException e) {
   e.printStackTrace();
  }


  while (true) {
   Socket s = null;

   try {
    s = ss.accept();

    Socket nsd_socket = new Socket(addressv4, nsd_port);
    DataInputStream nsd_dis = new DataInputStream(nsd_socket.getInputStream());
    DataOutputStream nsd_dos = new DataOutputStream(nsd_socket.getOutputStream());

    System.out.println("A new client is connected : " + s);

    // obtaining input and out streams
    DataInputStream dis = new DataInputStream(s.getInputStream());
    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

    System.out.println("Assigning new thread for this client");

    Thread t = new ClientHandler(s, nsd_socket, dis, nsd_dos, 1);
    Thread t1 = new ClientHandler(s, nsd_socket, nsd_dis, dos, 2);

    t.start();
    t1.start();


   } catch (Exception e) {
    s.close();
    e.printStackTrace();
    ss.close();
   }
  }



 }
}

class ClientHandler extends Thread {
 final DataInputStream dis;
 final DataOutputStream dos;
 final Socket s, nsd_socket;
 final int id;

 // Constructor
 public ClientHandler(Socket s, Socket nsd_Socket, DataInputStream dis, DataOutputStream dos, int t_id) {
  this.s = s;
  this.nsd_socket = nsd_Socket;
  this.dis = dis;
  this.dos = dos;
  this.id = t_id;
 }

 @Override
 public void run() {
  String received = "";
  String toreturn;
  byte[] readOneByte = new byte[1];
  try {
   while (true) {
    try {
     readOneByte[0] = dis.readByte();
     dos.write(readOneByte);
    } catch (Exception e) {
     try {
      this.dos.close();
      this.dis.close();
      return;
     } catch (Exception e1) {

     }
     return;
    }
   }
  } catch (Exception e) {
   throw e;
  }
 }
}