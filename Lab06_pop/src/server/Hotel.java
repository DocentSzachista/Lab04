package server;
import java.io.*;
import java.net.*;
import java.util.*;
import enums.*;
import room.Room;



/**
 * 
 * Hotel ma pokoje jedno, dwu i trzy osobowe. 
 * Liczba pokoi ka¿dego typu ma byæ parametryzowana. Stan pokoi powinien byæ widoczny na graficznym interfejsie. 
 * Hotel ma gniazdo serwerowe dzia³aj¹ce na znanym hoœcie i zadanym porcie (numer portu mo¿na wpisaæ na graficznym interfejsie).

    Hotel:
 *   przyjmuje ¿¹dania od pokoi o przyznanie numeru oraz portu (zawieraj¹ce informacjê o typie pokoju). 
 *   ¯¹danie te wysy³ane s¹, gdy pokoje startuj¹ (gdy pokoje s¹ "w³¹czane" do systemu hotelowego). 
 *   W odpowiedzi przesy³a numer portu, na jakim dany pokój otworzyæ ma gniazdo serwerowe. 
 *   Przy okazji uzupe³nia w³asn¹ listê dostêpnych pokoi. Tworzy te¿ gniazda klienckie do komunikacji z gniazdami serwerowymi pokoi.
 *   
 *   przyjmuje ¿¹dania od pokoi o ich usuniêcie z systemu hotelowego. 
 *   Reaguje na nie usuniêciem danego pokoju z listy dostêpnych pokoi i wys³aniem potwierdzenia tej czynnoœci 
 *   (hotel nie musi sprawdzaæ, czy pokój jest zajêty czy wolny).
 *   
 *   przyjmuje ¿¹dania dokonania rezerwacji przychodz¹ce z terminali goœci, zawieraj¹ce informacjê o nazwie goœcia, 
 *   liczbie i typach rezerwowanych pokoi (dla uproszczenia pomijamy terminy rezerwacji). 
 *   W odpowiedzi do ka¿dego takiego ¿¹dania wysy³a informacjê o dokonanej rezerwacji, zawieraj¹c¹ numery i klucze zarezerwowanych pokoi,
 *   host i porty, na których dzia³aj¹ pokoje. Jeœli rezerwacji nie mo¿na dokonaæ, w odpowiedzi przesy³a informajê o odrzuceniu rezerwacji.
 *    
 *   przyjmuje indywidualne ¿¹dania zakoñczenia pobytu od terminali goœci, zawieraj¹ce numery i klucze pokoi.
 *   W odpowiedzi przesy³a potwierdzenie zakoñczenia pobytu (i resetuje klucze pokoi) pod warunkiem, ¿e pokoje zosta³y zwolnione.
 *   W przeciwnym wypadku zwraca informacjê, ¿e zakoñczenie pobytu nie jest mo¿liwe z powodu zajmowania pokoi o podanych numerach
 *     
 *   przyjmuje ¿¹dania od pokoi zawieraj¹ce informacje o ich stanie (patrz opis pokoju i klienta).
 *
 *
 */

public class Hotel{
	//
	public ServerSocket serverSocket;
	private final static int  PORT_NUMBER=3500;
	private final static List<SubServer> clients= new ArrayList<SubServer>();
	//
	private static int ids=PORT_NUMBER;
	private List<Room> rooms;
	Thread r;
	HotelPanel panel;
	// bo przecie¿ trzeba jakoœ wrzuciæ dane do gui
	//private HotelPanel hotelTerminal;
	public Hotel(HotelPanel panel) throws IOException
	{
		this.panel=panel;
		rooms=new ArrayList<Room>();
	}
	public void startListening(int port) 
	{
		try
		{
			System.out.print("xD");
			this.serverSocket=new ServerSocket(port);
			
			r=new Thread(new Runnable()
			{
		@Override
		public void run()
		{
			try {
			
			Socket socket;
					while(true) 
					{
						//System.out.println("Server is listening on port : "+PORT_NUMBER);
						System.out.print("Oczekujê");
						
							socket=serverSocket.accept();
						 // server is in listening mode
						System.out.println("Request arrived..");// diverting the request to processor with the socket reference
						System.out.println("Connection from " + socket + "!");
						initialize(socket);
					}
				}
			catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}});
		
		r.start();
		
		}catch(Exception e)
		{
			System.out.println(e); 
		}
	}
	
	public void initialize(Socket socket)
	{
		clients.add(new SubServer(ids++, socket));
	}
	/**
	 * Klasa do przetwarzania kilku klientów naraz
	 * @author idont
	 *
	 */
	protected class SubServer extends Thread
	{
		private final int id;
		private Socket clientSocket;
		public SubServer(int id, Socket socket)
		{
			super(String.valueOf(ids));
			this.id=id;
			this.clientSocket=socket;
			start();
		}
		public void run() 
		{
			
				try {
					InputStream inStream= clientSocket.getInputStream();
					OutputStream outStream=clientSocket.getOutputStream();
					
					Scanner in = new Scanner(inStream, "UTF-8"); // scanner do zaczytywania infa od klienta
					PrintWriter out=new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true); //wysy³anie do klienta
					
					while(in.hasNextLine())
					{
						String clientInfo[]=in.nextLine().split(",");
						Execution command= Execution.valueOf(clientInfo[1]);
						switch(ClientType.valueOf(clientInfo[0])) 
						{
						
							case ROOM:
							{
									
									System.out.println(clientInfo);
									switch(command)
									{
										case DELETE:
										{
											this.clientSocket.close();
											System.out.println("Socket wy³¹czony");
											deleteMe();
											Thread.currentThread().interrupt();
											break;
										}
										case ADD:
										{
											String temp[]=clientInfo;
											out.println(ids);
											Room room= new Room(ids, Integer.valueOf(temp[3]), RoomState.valueOf(temp[4]));
											rooms.add(room);
											sendMessage("127.0.0.1", ids, "SUCCESS");
											addRow(room);
											break;
										}
										/*if will be needed
										case UPDATE:
										{
											break;
										}
										case READ:
										{
											break;
										}
										*/
									}
									System.out.println("¯¹danie od " +this.getName());
									printMe();
									break;
							}
							case CLIENT:
							{
								switch(command)
								{
									case ADD:{
										out.println("SERWER: Klient siê po³¹czy³");
										System.out.println("Zaczytano klienta "+clientInfo[2]);
										break;
									}
									//szukanie wolnego pokoju i danie dostêpu klientowi 
									case READ:{
										Room temp= findFreeRoom();
										if(temp!=null)
										{	
											sendMessage("127.0.0.1", temp.getId(), "RESERVED,"+temp.getKey() );
											out.println("RESERVED,"+temp.getId()+","+temp.getRoomAmount()+","+temp.getRoomState()+","+temp.getKey());
											System.out.println("Zaczytano "+clientInfo[2]);
										}
										else
										{
											out.println("NONE");
										}
											break;
									}
									//zwalnianie pokoju 
									case DELETE:{
										int port= Integer.valueOf(clientInfo[2]);
										int temp =  freeRoom(port);
										if(temp!=-1)
										{
											sendMessage("127.0.0.1", port, "UPDATE,"+temp);
											out.println("SUKCES");
										}
										break;
									}
									//Wchodzenie i wychodzenie z pokoju
									case UPDATE:{
										int port=Integer.valueOf(clientInfo[3]);
										if(clientInfo[2]=="OUT")
										{
											sendMessage("127.0.0.1", port, "ROOM,OUT");
											out.println("SUCKCES");
										}
										else
										{
											sendMessage("127.0.0.1", port, "ROOM,"+clientInfo[2]);
											out.println("SUKCES");
										}
										break;
									}
								}
								break;
							}
						}	
						refreshTable();
					}
					System.out.print("Wyszed³em");
				}
				catch(IOException e)
				{
					e.printStackTrace();
					
				} /*catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			
		}
		public void addRow(Room room)
		{
		 Object row[]=new Object[] {room.getId(), room.getRoomAmount(), room.getRoomState(), room.getKey()};
		 panel.getModel().addRow(row);
		}
		public void refreshTable()
		{
			panel.getModel().setNumRows(0);
			for(Room room: rooms)
			{
				Object row[]= new Object[] {room.getId(), room.getRoomAmount(), room.getRoomState(), room.getKey()};
				panel.getModel().addRow(row);
			}
		}
		/*
		public void editRow(String id, String[] args)
		{
			for(int i=0; i<panel.getModel().getRowCount(); i++)
			{
				String temp= String.valueOf(panel.getTable().getValueAt(i, 0));
				if(temp.contains(id))
				{
					if(args.length==2)
					{
						panel.getTable().setValueAt(args[0], i, 2);
						panel.getTable().setValueAt(args[2], i, 1);
					}
					else
					{
						panel.getTable().setValueAt(args[0], i, 1);
					}
				}
			}
			
		}
		*/
		/*******************************************************************************************************************
		 *************************************************** Imitacje GUI***************************************************
		 */
		public void printMe()
		{
			System.out.println("id, iloœæ pokoi, STAN");
			for(Room room: rooms)
			{
				System.out.println(room.toString());
			}
		}
		public void deleteMe()
		{
			for(Room room: rooms)
			{
				if(room.getId()==Integer.valueOf(this.getName()))
				{
					rooms.remove(room);
					break;
				}
			}
		}
		/**************************************************************************************************************************
		 * **************************************************Wysy³anie informacji dalej********************************************
		 * ************************************************************************************************************************
		
		 */
		public void sendMessage(String toWho, int ip, String mess)
		{
			try {
				Socket s= new Socket(toWho, ip);
				OutputStream outStreamMethod=s.getOutputStream();
				PrintWriter toMessenger=new PrintWriter(new OutputStreamWriter(outStreamMethod, "UTF-8"), true);
				toMessenger.println(mess);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		private Room findFreeRoom()
		{
			for(int i=0; i<rooms.size();i++ )
			{
				if(rooms.get(i).getRoomState().equals(RoomState.FREE))
				{
					rooms.get(i).setKey(23241);
					rooms.get(i).setRoomState(RoomState.BUSY);
					return rooms.get(i);
				}
			}
			return null;
		}
		private int freeRoom(int port)
		{
			for(int i=0; i<rooms.size();i++ )
			{
				if(rooms.get(i).getId()==port)
				{
					rooms.get(i).setRoomState(RoomState.FREE);
					rooms.get(i).setKey(0000);
					return 0000;
				}
			}
			return -1;
		}
		
		
		
		
		
	}

	
	public static void main(String args[]) throws IOException
	{
		//Hotel hotel= new Hotel();
		//hotel.startListening();
		
	}
}