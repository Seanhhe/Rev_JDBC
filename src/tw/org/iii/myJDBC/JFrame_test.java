package tw.org.iii.myJDBC;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class testJFrame extends JFrame {
	
	public testJFrame() {
		JButton btn = new JButton("按我");
		JLabel idLabel = new JLabel("帳號 : ");
		JTextField id = new JTextField(10); // 10行寬度
		JPanel top = new JPanel();
		
		setLocation(200, 100); // 設定視窗座標原點
		setLayout(new BorderLayout());
		top.add(idLabel); top.add(id);
		
		add(top, BorderLayout.NORTH);
		add(btn, BorderLayout.CENTER);
		
		
		
		setTitle("測試視窗");
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
}

public class JFrame_test {

	public static void main(String[] args) {
		new testJFrame();
	}

}
