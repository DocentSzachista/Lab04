package client;

import javax.swing.JPanel;

import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;



import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import enums.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
public class ClientPanel extends JPanel {
	private JTable reservedTable;
	private DefaultTableModel model;
	private JTextField credentials;
	private ClientGuy client;
	private JTextField givePort;
	
	
	private JButton reserveRoom;
	private JButton freeRoom;
	private JButton walkIn;
	private JButton goOut;
	/**
	 * Create the panel.
	 */
	public ClientPanel() {
		setLayout(new GridLayout(1, 2, 0, 0));
		client=new ClientGuy("xD", this);
		model=new DefaultTableModel();
		reservedTable = new JTable(model);
		Object[] column= {"id pokoju", "Iloœæ ³ózek","Stan","Klucz","wszed³?"};
		model.setColumnIdentifiers(column);
		JScrollPane scrollPane = new JScrollPane(reservedTable);
		add(scrollPane);
		
		JPanel buttonPanel = new JPanel();
		add(buttonPanel);
		buttonPanel.setLayout(new GridLayout(4, 2, 0, 0));
		/*****************************************
		 * ***************************************************ZWOLNIENIE POKOJU
		 */
		 freeRoom = new JButton("Zwolnij pok\u00F3j");
		 freeRoom.addActionListener(new ActionListener() {
		 	public void actionPerformed(ActionEvent e) {
		 		int row= reservedTable.getSelectedRow();
		 		if(row!=-1)
		 		{
		 			String id= String.valueOf(reservedTable.getValueAt(row, 0));
		 			client.sendData(Execution.DELETE+","+id);
		 			model.removeRow(reservedTable.getSelectedRow());
		 			JOptionPane.showMessageDialog(null, "Zwolniono pokój");
		 		}
		 		else
		 		{
		 			JOptionPane.showMessageDialog(null, "Nie wybrano wiersza tabeli ");
		 		}
		 	}
		 });
		buttonPanel.add(freeRoom);
		 
		/*****************************************
		 * ***************************************************REZERWACJA POKOJU
		 */
		reserveRoom = new JButton("Zarezerwuj pok\u00F3j");
		reserveRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//send.send(ClientType.CLIENT+","+"Rezerwacja", "127.0.0.1", 3800);
				client.sendData(String.valueOf(Execution.READ+","+"test"));
				freeRoom.setEnabled(true);
				walkIn.setEnabled(true);
				goOut.setEnabled(true);
			}
		});
		buttonPanel.add(reserveRoom);
		/*****************************************
		 * ***************************************************WEJŒCIE DO POKOJU
		 */
		walkIn = new JButton("Wejdz do pokoju");
		walkIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row= reservedTable.getSelectedRow();
		 		if(row!=-1)
		 		{
		 			String id= String.valueOf(reservedTable.getValueAt(row, 0));
		 			client.sendData(Execution.UPDATE+","+client.getHisData()+","+id);
		 			goOut.setEnabled(true);
		 			reservedTable.setValueAt("IN", row, 4);
		 		}
		 		else
		 		{
		 			JOptionPane.showMessageDialog(null, "Nie wybrano wiersza tabeli ");
		 		}

			}
		});
		buttonPanel.add(walkIn);
		
		goOut = new JButton("Wyjdz z pokoju");
		goOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row= reservedTable.getSelectedRow();
		 		if(row!=-1)
		 		{
		 			String id= String.valueOf(reservedTable.getValueAt(row, 0));
		 			client.sendData(Execution.UPDATE+","+"OUT,"+id);
		 			reservedTable.setValueAt("OUT", row, 4);
		 		}
		 		else
		 		{
		 			JOptionPane.showMessageDialog(null, "Nie wybrano wiersza tabeli ");
		 		}
			}
		});
		buttonPanel.add(goOut);
		
		credentials = new JTextField();
		buttonPanel.add(credentials);
		credentials.setColumns(10);
		/*****************************************
		 * ***************************************************PODANIE DANYCh
		 */
		JButton credentialsButton = new JButton("Podaj swoje dane i pod\u0142\u0105cz si\u0119");
		credentialsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sth=credentials.getText();
				int port= Integer.valueOf(givePort.getText());
				
				client.setHisData(sth);
				client.startConnection(port);
				
				client.sendData(Execution.ADD+","+client.toString());
				credentials.setEnabled(false);
				givePort.setEnabled(false);
				credentialsButton.setEnabled(false);
				credentialsButton.setBackground(Color.RED);
				reserveRoom.setEnabled(true);
			}
		});
		buttonPanel.add(credentialsButton);
		
		givePort = new JTextField();
		givePort.setText("3500");
		buttonPanel.add(givePort);
		givePort.setColumns(10);
		
		JLabel ipLabel = new JLabel("Wpisz tutaj port");
		buttonPanel.add(ipLabel);
		
		reserveRoom.setEnabled(false);
		freeRoom.setEnabled(false);
		walkIn.setEnabled(false);
		goOut.setEnabled(false);
	}
	public DefaultTableModel getModel()
	{
		return model;
	}
}
