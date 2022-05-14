import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JScrollPane;
public class usuariosBase {

	private JFrame frame;
	private JComboBox<String> comboBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					usuariosBase window = new usuariosBase();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public usuariosBase() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	//La base de datos debera ser modificada segun el puerto que tengamos configurado asi como el usuario y contraseña
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 708, 515);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(21, 35, 171, 22);
		frame.getContentPane().add(comboBox);//Primer comboBox con 2 opciones, se puede meter directo de la base de datos
		comboBox.addItem("Jese");
		comboBox.addItem("Román");
		
		JLabel lblNewLabel = new JLabel("Selecciona el usuario");
		lblNewLabel.setBounds(21, 10, 171, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JTextArea textArea_1 = new JTextArea();//Donde se va a crear la lista de usuarios activos
		textArea_1.setEditable(false);
		textArea_1.setBounds(10, 93, 672, 72);
		frame.getContentPane().add(textArea_1);
		
		JComboBox comboBox_1 = new JComboBox();//Segundo comboBox donde se va a mostrar los equipos, igual se puede sacar de la ase de datos
		comboBox_1.setBounds(214, 35, 171, 22);
		frame.getContentPane().add(comboBox_1);
		comboBox_1.addItem("iMac #1");
		comboBox_1.addItem("iMac #2");
		
		JButton btnNewButton_1 = new JButton("Seleccionar");//Selecciona el usuario y el equipo para dar de alta como activo
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textArea_1.setText(null);
				String usuario=(String) comboBox.getSelectedItem();
				String equipo=(String) comboBox_1.getSelectedItem();
				int id_usuario=0, id_equipo=0,total=0;
				DateTimeFormatter hora = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");//para seleccionar la hora actual y registrarla
				try {//se agrega todo en un try por si llega a fallar algo no cierre el programa de golpe
					Class.forName("com.mysql.jdbc.Driver");//driver para leer la base de datos
					Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Empresa","root","");
					Statement stmt=con.createStatement();
					String baja="UPDATE movimientos SET fin=\"INACTIVO\" WHERE Nom_equipo=\""+equipo+"\"";
					stmt.executeUpdate(baja);//para dar de baja en caso de repetir dispositivo
					ResultSet obid_usuario=stmt.executeQuery("SELECT ID FROM usuarios WHERE Nombre=\""+usuario+"\"");
					while(obid_usuario.next()) {
						id_usuario=obid_usuario.getInt(1);
					}
					ResultSet obid_equipo=stmt.executeQuery("SELECT Serie FROM equipos WHERE Nombre_Equipo=\""+equipo+"\"");
					while(obid_equipo.next()) {
						id_equipo=obid_equipo.getInt(1);
					}
					
					ResultSet cuenta=stmt.executeQuery("SELECT COUNT(*) FROM movimientos");
					while(cuenta.next()) {//añadir un id a los registros que no se repitan y tener mejor organizada la lista
						total=cuenta.getInt(1);
						total++;
					}
					String Iniciar="INSERT INTO movimientos(No_registro,ID_usuario, Nom_usuario, ID_equipo, Nom_equipo, inicio, fin) VALUES ("+total+","+id_usuario+",\""+ usuario +"\","+id_equipo+",\""+equipo+"\",\""+hora.format(LocalDateTime.now())+"\",\"ACTIVO\");";
					
					stmt.executeUpdate(Iniciar);//agregar al registro el nuevo usuario activo
					
					ResultSet rs=stmt.executeQuery("select * from movimientos where fin=\"ACTIVO\"");
					while(rs.next()) {
						textArea_1.append((rs.getInt(1)+"   "+rs.getInt(2)+"\t"+rs.getString(3)+"\t"+rs.getInt(4)+"\t"+rs.getString(5)+"\t"+rs.getString(6)+"\t"+rs.getString(7)+"\n"));
					}
				
					con.close();
				}catch(Exception r) {//mostarar este error en caso de que no se pueda acceder a la base de datos
					System.out.println(r);
				}
					
			}
		});
		
		btnNewButton_1.setBounds(395, 35, 106, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		JLabel lblSeleccionaElEquipo = new JLabel("Selecciona el equipo");
		lblSeleccionaElEquipo.setBounds(214, 10, 171, 14);
		frame.getContentPane().add(lblSeleccionaElEquipo);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 266, 672, 158);
		frame.getContentPane().add(scrollPane);

		
		JTextArea textArea = new JTextArea();//aqui se va a mostrar el area de impresion de historial
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		
		JButton btnNewButton_2 = new JButton("Actualizar");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(null);//actualiza los valores para no mostrar repetidos
				try {//de igual forma por si llega a fallar
					Class.forName("com.mysql.jdbc.Driver");
					Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Empresa","root","");
				
					Statement stmt=con.createStatement();
					ResultSet rs=stmt.executeQuery("select * from movimientos");//muestra el historial completo
					while(rs.next()) {
						textArea.append((rs.getInt(1)+"   "+rs.getInt(2)+"\t"+rs.getString(3)+"\t"+rs.getInt(4)+"\t"+rs.getString(5)+"\t"+rs.getString(6)+"\t"+rs.getString(7)+"\n"));
					}
					con.close();
				}catch(Exception r) {
					System.out.println(r);
				}
			}
		});
		btnNewButton_2.setBounds(10, 232, 119, 23);
		frame.getContentPane().add(btnNewButton_2);
		
		JLabel lblNewLabel_1 = new JLabel("Historial de movimientos");
		lblNewLabel_1.setBounds(10, 207, 203, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Usuarios activos");
		lblNewLabel_2.setBounds(10, 68, 171, 14);
		frame.getContentPane().add(lblNewLabel_2);
		
		JButton btnNewButton = new JButton("Salir");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {//try en caso de fallo
				textArea_1.setText(null);
				String usuario=(String) comboBox.getSelectedItem();
				int id_usuario=0, id_equipo=0,total=0;
				DateTimeFormatter hora = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				try {
					Class.forName("com.mysql.jdbc.Driver");
					Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Empresa","root","");
					Statement stmt=con.createStatement();
					String baja="UPDATE movimientos SET fin=\"INACTIVO\" WHERE Nom_usuario=\""+usuario+"\"";
					stmt.executeUpdate(baja);//da de baja el usuario seleccionado para simular un LogOut y quitar el estado activo
					ResultSet rs=stmt.executeQuery("select * from movimientos where fin=\"ACTIVO\"");//vuelve a mostrar solo los activos
					while(rs.next()) {
						textArea_1.append((rs.getInt(1)+"   "+rs.getInt(2)+"\t"+rs.getString(3)+"\t"+rs.getInt(4)+"\t"+rs.getString(5)+"\t"+rs.getString(6)+"\t"+rs.getString(7)+"\n"));
					}
					con.close();
				}catch(Exception r) {
					System.out.println(r);
				}
				
			}
		});
		btnNewButton.setBounds(511, 35, 89, 23);
		frame.getContentPane().add(btnNewButton);
	}
	public JComboBox getComboBox() {
		return comboBox;
	}
}