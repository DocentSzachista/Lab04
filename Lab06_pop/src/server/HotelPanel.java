package server;

import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HotelPanel extends JPanel {
	private JTable table;
	private Hotel hotel;
	private DefaultTableModel model;
	private JTextField portField;
	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public HotelPanel() throws IOException {
	
		hotel=new Hotel(this);
		
		
		model=new DefaultTableModel();
		table = new JTable(model);
		Object[] column= {"id pokoju", "Iloœæ ³ózek","Stan","Klucz","wszed³?"};
		model.setColumnIdentifiers(column);
		setLayout(new GridLayout(0, 2, 0, 0));
		
		JScrollPane scrollPaneTable = new JScrollPane(table);
		scrollPaneTable.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		add(scrollPaneTable);
		
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(null);
		
		portField = new JTextField();
		portField.setBounds(0, 74, 225, 44);
		panel.add(portField);
		portField.setColumns(10);
		
		JButton connectButton = new JButton("po\u0142\u0105cz si\u0119 na wpisanym porcie");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int port=Integer.parseInt(portField.getText());
				hotel.startListening(port);
				//hotel.startListening();
				portField.setEditable(false);
				connectButton.setEnabled(false);
				connectButton.setBackground(Color.RED);
			}
		});
		connectButton.setBounds(0, 128, 225, 67);
		panel.add(connectButton);
		
	}

	public DefaultTableModel getModel()
	{
		return model;
	}
	public JTable getTable()
	{
		return table;
	}
}
