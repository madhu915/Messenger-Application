import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
import java.util.Scanner;


//run --> java group_chat 239.0.0.0 1234
public class group_chat
{
	private static final String TERMINATE = "Exit";
	static String name;
	static volatile boolean finished = false;
	public static Scanner sc = new Scanner(System.in);

	public static void main(String[] args)
	{		
			try
			{
				InetAddress group = InetAddress.getByName(args[0]);
				int port = Integer.parseInt(args[1]);
                InetSocketAddress sgroup=new InetSocketAddress(group,port);
				System.out.print("Enter your name: ");
				name = sc.nextLine();
				MulticastSocket socket = new MulticastSocket(port);
                NetworkInterface nfc=socket.getNetworkInterface();
			
				// Since we are deploying
				socket.setTimeToLive(0);
				//this on localhost only (For a subnet set it as 1)
				
				socket.joinGroup(sgroup, nfc);
				Thread t = new Thread(new
				ReadThread(socket,group,port));
			
				// Spawn a thread for reading messages
				t.start();
				
				// sent to the current group
				System.out.println("Press \"EXIT\" to stop chatting...\n");
				while(true)
				{
					String message;
					message = sc.nextLine();
					if(message.equalsIgnoreCase(group_chat.TERMINATE))
					{
						finished = true;
						socket.leaveGroup(sgroup, nfc);
						socket.close();
						break;
					}
					message = name + ": " + message;
					byte[] buffer = message.getBytes();
					DatagramPacket datagram = new
					DatagramPacket(buffer,buffer.length,group,port);
					socket.send(datagram);
				}
			}
			catch(SocketException se)
			{
				System.out.println("Error creating socket");
				se.printStackTrace();
			}
			catch(IOException ie)
			{
				System.out.println("Error reading/writing from/to socket");
				ie.printStackTrace();
			}
		
	}

	// public static void main(String[] args) {
	// 	// DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //     // System.out.println("yyyy/MM/dd HH:mm:ss-> "+dtf.format(LocalDateTime.now()));


	// }
}
class ReadThread implements Runnable
{
	private MulticastSocket socket;
	private InetAddress group;
	private int port;
	private static final int MAX_LEN = 1000;
	ReadThread(MulticastSocket socket,InetAddress group,int port)
	{
		this.socket = socket;
		this.group = group;
		this.port = port;
	}
	
	@Override
	public void run()
	{
		while(!group_chat.finished)
		{
				byte[] buffer = new byte[ReadThread.MAX_LEN];
				DatagramPacket datagram = new
				DatagramPacket(buffer,buffer.length,group,port);
				String message;
			try
			{
				socket.receive(datagram);
				message = new
				String(buffer,0,datagram.getLength(),"UTF-8");
				if(!message.startsWith(group_chat.name))
					System.out.println(message);
			}
			catch(IOException e)
			{
				System.out.println("You have left the chat!");
			}
		}
	}
}
