package room;


import java.io.*;
import java.net.*;
import java.util.Scanner;
import enums.*;
import client.ClientGuy;

/***************************************************************************************************************************************
 *    zwraca się do hotelu o przyznanie numeru oraz portu wysyłając odpowiednio zredagowane żądanie 
 *   (zakładamy, że room utworzy gniazdo serwerowe na tym samym hoście co hotel, z numerem portu pozyskanym z hotelu).
 *   
 *    przyjmuje od hotelu żądania aktualizacji klucza.
 *   
 *    informuje hotel o swoim stanie, wysyłając odpowiednio zredagowane żądanie. 
 *    
 *    Stan pokoju zmienia się na skutek obsługi żadania przychodzącego z terminala klienta.
 *    
 *    informuje hotel o swoim "wyłączeniu" z systemu hotelowego (żądanie to może być wysłane np. podczas zamykania instancji pokoju).
 *    
 *    przyjmuje żadania z terminali gości, zawierające informacje o numerze pokoju oraz 
 *    kluczu (parametry te muszą się zgadzać z przypisanym do pokoju numerem i kluczem, 
 *    inaczej powinno nastąpić odrzucenie żądania) oraz informacje o stanie 
 *    (jeśli gość "wchodzi" do pokoju, to zmienia stan pokoju na "zajęty", jeśli "wychodzi" z pokoju, to zmienia stan pokoju na "wolny".
 * 
 *
 */
public class Room {
	
	/*
	 * atrybuty do połączenia
	 */
	private final static String LOCALHOST_IP="127.0.0.1";
	private final static int SERVER_PORT=3500; // port do gniazda serwera
	private Socket socket; //port który zostanie przydzielony przez serwer.
	private final static ClientType type= ClientType.ROOM;
	private boolean connected;
	private InputStream inStream;
	private OutputStream outStream;
	private Scanner in;
	private PrintWriter out;
	//GNIAZDKO SERWEROWE
	Thread r;
	ServerSocket roomServ;
	/*
	 * atrybuty pokoju
	 */
	private int roomAmount;//ilość osób
	private int roomId; //indentyfikator pokoju
	private int key;//przyznawane przez hotel
	private RoomState roomState; //stan pokoju 
	private ClientGuy roomMate;
	
	private RoomTerminal panel;
	/*
	 * Konstruktor dla serwera
	 */
	public Room(int id, int roomAmount, RoomState roomState)
	{
		this.key=0;
		this.roomAmount=roomAmount;
		this.roomId=id;
		this.setRoomState(roomState);
		this.connected=false;
	}
	/*
	 * Konstruktor dla GUI
	 */
	public Room(int id, int roomAmount, RoomState roomState, RoomTerminal room)
	{
		this.panel=room;
		this.key=0;
		this.roomAmount=roomAmount;
		this.roomId=id;
		this.setRoomState(roomState);
		this.connected=false;
	}
	
	public boolean startConnection(Execution message ) throws IOException
	{
	  	 socket = new Socket(LOCALHOST_IP, SERVER_PORT);
	    // get the output stream from the socket.
	  	
	  	 inStream= socket.getInputStream(); // strumień do odczytu
		 outStream=socket.getOutputStream(); //strumień do wpisywania
		  in = new Scanner(inStream, "UTF-8"); // scanner do zaczytywania infa od serwera
		  out=new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true); //wysyłanie do serwera
		
		
		
		System.out.println(message+this.toString());
		out.println(type+","+message+","+this.toString());
		this.setId(in.nextInt());
		r=new Thread(new Runnable()
				{
			@Override
			public void run()
			{
				boolean flag=true;
				try {
					System.out.println("centrala recivint on");
					roomServ = new ServerSocket(roomId);
					while (flag) {
						Socket sc = roomServ.accept();
						InputStream is = sc.getInputStream();
						InputStreamReader isr = new InputStreamReader(is);
						Scanner input= new Scanner(is, "UTF-8");
							String line= input.nextLine();
							System.out.println("Wiadomość od serwera" + line);
							String theLine[]=line.split(",");
							switch(theLine[0])
							{
								case"SUCCESS": 
								{
									System.out.println("Sukces");
									break;
								}
								case "UPDATE":
								{
									setKey(Integer.valueOf(theLine[1]));
									panel.getKeyField().setText(String.valueOf(key));
									break;
								}
								case "RESERVED":{
									if(key==0)
									{
										System.out.println(line);
										setKey(Integer.valueOf(theLine[1]));
										panel.getKeyField().setText(String.valueOf(key));
									}
									else
									{
										//TODO pokój zarezerwowany you cant fcking reserve it
										
									}
									break;
								}
								case "ROOM":
								{
									panel.getStatusLabel().setText(theLine[1]);
								}
							}
						sc.close();
						
					}
					roomServ.close();
				} catch (SocketException e) {
					// TODO - podczas przerywania w�tku metoda accept zg�osi wyj�tek
					// z wiadomo�ci�: socket closed
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				}
		);
		r.start();
		
		return true;
	}
	public boolean endConnection() throws IOException
	{ 
		out.println(type+","+Execution.DELETE);
		if(!roomServ.isClosed())
		{
			roomServ.close();
		}
		return true;
	}
	
	
	
	
	
	public boolean informAboutState()
	{
		return true;
	}
	public int getId()
	{
		return roomId;
	}
	public void setId(int roomId)
	{
		this.roomId=roomId;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public ClientGuy getRoomMate() {
		return roomMate;
	}
	public void setRoomMate(ClientGuy roomMate) {
		this.roomMate = roomMate;
	}
	public RoomState getRoomState() {
		return roomState;
	}
	public void setRoomState(RoomState roomState) {
		this.roomState = roomState;
	}
	public int getRoomAmount()
	{
		return this.roomAmount;
	}
	public String toString()
	{
		return this.roomId+","+this.roomAmount+","+this.roomState;
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
