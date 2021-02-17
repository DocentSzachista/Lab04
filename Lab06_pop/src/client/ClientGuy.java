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
 * Terminal go�cia reprezentuje aplikacj�, za pomoc� kt�rej go�� mo�e zarezerwowa� pok�j w hotelu, a p�niej z niego korzysta�. 
 *   Terminal go�cia wie, na jakim ho�cie i porcie dzia�a hotel. 
 *   Informacje o tym, na jakich portach dzia�aj� pokoje otrzymuje w odpowiedzi na ��danie rezerwacji.
 *   Terminal:
 *   oferuje graficzny interfejs, za pomoc� kt�rego go�� mo�e wysy�a� ��dania rezerwacji pokoi oraz ��danie zako�czenia pobytu.
 *   wy�wietla listy zarezerwowanych przez go�cia pokoi i ich stany.
 *   umo�liwia "wej�cie" i "wyj�cie" z danego pokoju poprzez przes�anie odpowiednio zredagowanego ��dania do odpowiedniego pokoju.
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
			inStream= socket.getInputStream(); // strumie� do odczytu
			outStream=socket.getOutputStream(); //strumie� do wpisywania
			in = new Scanner(inStream, "UTF-8"); // scanner do zaczytywania infa od serwera
			out=new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true); //wysy�anie do serwera
			System.out.println("Nawi�zano po��czenie z serwerem");
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
			System.out.println("Jest roz��czony od dawna");
		}
	}
	public String toString()
	{
		return this.id+","+this.hisData;
	}
}
