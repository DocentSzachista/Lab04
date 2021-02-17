package room;

import javax.swing.JPanel;

import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import enums.*;
import java.awt.GridLayout;
public class RoomTerminal extends JPanel {
	
	/**TODO zrobiæ listenery, które bêd¹ wypluwaæ okienka informuj¹ce o przyjêciu serwera b¹d¿ jego wy³¹czeniu
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JButton btnNewButton;
	private Room room;
	
	private JLabel roomIdInfo;
	private JLabel lblNewLabel_1;
	private JTextField keyText;
	private JLabel lable;
	private JLabel statusLabel;
	/**
	 * Create the panel.
	 */
	public RoomTerminal() {
		room= new Room( 0,  randomize(3,1),  RoomState.FREE, this);
		setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel roomSize = new JLabel("ilo\u015B\u0107 \u0142\u00F3\u017Cek");
		add(roomSize);
		textField = new JTextField();
		add(textField);
		textField.setColumns(10);
		
		btnNewButton = new JButton("Ask server to give Id");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!room.isConnected())
				{
					try {
						room.startConnection(Execution.ADD);
						roomIdInfo.setText(String.valueOf(room.getId()));
						room.setConnected(true);
						textField.setText(String.valueOf(room.getRoomAmount()));
						JOptionPane.showMessageDialog(null, "Pod³¹czony do serwera");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else 
				{
					JOptionPane.showMessageDialog(null, "Jesteœ ju¿ pod³¹czony do serwera");
				}
			}
		});
		
		JLabel roomId = new JLabel("id Pokoju");
		add(roomId);
		
		roomIdInfo = new JLabel("");
		add(roomIdInfo);
		
		JButton deleteMeButton = new JButton("Usu\u0144 mnie z rejestru");
		deleteMeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//room.startConnection(Execution.DELETE);
					room.endConnection();
					room.setConnected(false);
					JOptionPane.showMessageDialog(null, "Od³¹czony do serwera");
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		lblNewLabel_1 = new JLabel("Klucz do pokoju");
		add(lblNewLabel_1);
		
		keyText = new JTextField();
		add(keyText);
		keyText.setColumns(10);
		
		lable = new JLabel("Czy kto\u015B jest w \u015Brodku");
		add(lable);
		
		statusLabel = new JLabel("NIE");
		add(statusLabel);
		add(deleteMeButton);
		add(btnNewButton);
	}
	
	
	public  int randomize(int max, int min)
	{
		return min+(int)(Math.random() * max+1 - min);
	}
	public JTextField getKeyField ()
	{
		return keyText;
	}
	public JLabel getStatusLabel()
	{
		return statusLabel;
	}
	
}
