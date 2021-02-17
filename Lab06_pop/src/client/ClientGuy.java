package client;
import java.util.*;

import javax.swing.JOptionPane;

import room.Room;
import enums.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
/*
 * Terminal goœcia reprezentuje aplikacjê, za pomoc¹ której goœæ mo¿e zarezerwowaæ pokój w hotelu, a póŸniej z niego korzystaæ. 
 *   Terminal goœcia wie, na jakim hoœcie i porcie dzia³a hotel. 
 *   Informacje o tym, na jakich portach dzia³aj¹ pokoje otrzymuje w odpowiedzi na ¿¹danie rezerwacji.
 *   Terminal:
 *   oferuje graficzny interfejs, za pomoc¹ którego goœæ mo¿e wysy³aæ ¿¹dania rezerwacji pokoi oraz ¿¹danie zakoñczenia pobytu.
 *   wyœwietla listy zarezerwowanych przez goœcia pokoi i ich stany.
 *   umo¿liwia "wejœcie" i "wyjœcie" z danego pokoju poprzez przes³anie odpowiednio zredagowanego ¿¹dania do odpowiedniego pokoju.
 *
 * 
 */
public class ClientGuy {
	private String hisData;
	private int id;
	private List<Room> roomList;
	private Socket socket;
	private InputStream inStream;
	private OutputStream outStream;
	private Scanner in;
	private PrintWriter out;
	private ClientType type= ClientType.CLIENT;
	private ClientPanel panel;
	public ClientGuy(String  hisData, ClientPanel panel )
	{
		this.panel=panel;
		this.id=0;
		this.setHisData(hisData);
		setRoomList(new ArrayList<Room>());
	}
	public void setData(String data)
	{
		this.setHisData(data);
	}
	public List<Room> getRoomList() {
		return roomList;
	}
	public void setRoomList(List<Room> roomList) {
		this.roomList = roomList;
	}
	public String getHisData() {
		return hisData;
	}
	public void setHisData(String hisData) {
		this.hisData = hisData;
	}
	public void startConnection(int port)
	{
		try 
		{
			socket=new Socket("127.0.0.1", port);
			inStream= socket.getInputStream(); // strumieñ do odczytu
			outStream=socket.getOutputStream(); //strumieñ do wpisywania
			in = new Scanner(inStream, "UTF-8"); // scanner do zaczytywania infa od serwera
			out=new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true); //wysy³anie do serwera
			System.out.println("Nawi¹zano po³¹czenie z serwerem");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendData(String message)
	{
		out.println(type+","+message);
		String line=in.nextLine();
		System.out.println(line);
		if(line.contains("RESERVED"))
		{
			String tem[]=line.split(",");
			Object[] temp= {tem[1], tem[2], tem[3], tem[4]};
			panel.getModel().addRow(temp);
		}
		if(line.contains("NONE"))
		{
			JOptionPane.showMessageDialog(null, "Nie ma wolnych pokoi");
		}
	}
	public void closeConnection()
	{
		if(socket.isConnected())
		{
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		else
		{
			System.out.println("Jest roz³¹czony od dawna");
		}
	}
	public String toString()
	{
		return this.id+","+this.hisData;
	}
}
