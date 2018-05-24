package com.zhangxun.component;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.zhangxun.Main;

public class ColumnSetting extends JFrame implements ActionListener {
	private static final long serialVersionUID = -5473788932682692935L;

	private JButton defaultButton;
	private JButton allChooseButton;
	private JButton noChooseButton;
	private JButton closeButton;

	private static List<String> defaultIntegers = Arrays
			.asList(new String[] { "会计年度","会计期间","科目代码", "科目名称", "原币金额", "借方", "贷方", "凭证摘要", "分录序号", "核算项目" });
	private JCheckBox[] checkBoxs = new JCheckBox[Main.columnNames.length];

	private Main main;

	/**
	 * 初始化
	 */
	public ColumnSetting(Main main) {
		this.main = main;
		setSize(750, 320);
		setBackground(Color.WHITE);
		setTitle("选择展示列");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(null);
		setExtendedState(JFrame.NORMAL);
		setResizable(false);
		setLocationRelativeTo(null);
		setUndecorated(false);
		initComponent();
		setVisible(true);
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		JPanel jPanel = new JPanel(new GridLayout(0, 6));
		jPanel.setBounds(24, 10, 700, 220);
		for (int i = 0; i < Main.columnNames.length; i++) {
			JCheckBox ck = new JCheckBox(Main.columnNames[i], defaultIntegers.contains(Main.columnNames[i]));
			ck.addItemListener(new MyItemListener(main));
			checkBoxs[i] = ck;
			jPanel.add(ck);
		}

		add(jPanel);

		defaultButton = new JButton("默认");
		defaultButton.setBounds(30, 250, 80, 30);
		defaultButton.addActionListener(this);
		add(defaultButton);

		allChooseButton = new JButton("全选");
		allChooseButton.setBounds(120, 250, 80, 30);
		allChooseButton.addActionListener(this);
		add(allChooseButton);

		noChooseButton = new JButton("全不选");
		noChooseButton.setBounds(210, 250, 80, 30);
		noChooseButton.addActionListener(this);
		add(noChooseButton);

		closeButton = new JButton("关闭");
		closeButton.setBounds(300, 250, 80, 30);
		closeButton.addActionListener(this);
		add(closeButton);

		defaultButton.doClick();
	}

	/**
	 * 事件监听
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == defaultButton) {
			for (int i = 0; i < checkBoxs.length; i++) {
				if (defaultIntegers.contains(checkBoxs[i].getText())) {
					checkBoxs[i].setSelected(true);
				} else {
					checkBoxs[i].setSelected(false);
				}
			}
		} else if (e.getSource() == allChooseButton) {
			for (int i = 0; i < checkBoxs.length; i++) {
				if (!checkBoxs[i].isSelected()) {
					checkBoxs[i].setSelected(true);
				}
			}
		} else if (e.getSource() == noChooseButton) {
			for (int i = 0; i < checkBoxs.length; i++) {
				if (checkBoxs[i].isSelected()) {
					checkBoxs[i].setSelected(false);
				}
			}
		} else if (e.getSource() == closeButton) {
			setVisible(false);
		}
	}

	public static List<String> getDefaultIntegers() {
		return defaultIntegers;
	}

}

class MyItemListener implements ItemListener {

	private Main main;

	public MyItemListener(Main main) {
		this.main = main;
	}

	public void itemStateChanged(ItemEvent e) {
		JCheckBox jcb = (JCheckBox) e.getItem();// 将得到的事件强制转化为JCheckBox类
		int num = ((DefaultTableModel) (main.getTable().getModel())).findColumn(jcb.getText());
		TableColumn tableColumn = main.getTable().getColumnModel().getColumn(num);
		TableColumn column_id_header = main.getTable().getTableHeader().getColumnModel().getColumn(num);
		if (jcb.isSelected()) {// 判断是否被选择
			tableColumn.setMinWidth(100);
			//tableColumn.setWidth(100);
			tableColumn.setPreferredWidth(100);
			//tableColumn.setMaxWidth(100);
			//column_id_header.setMaxWidth(100);
			column_id_header.setPreferredWidth(100);
			column_id_header.setMinWidth(100);
		} else {
			tableColumn.setMinWidth(0);
			tableColumn.setPreferredWidth(0);
			//tableColumn.setMaxWidth(0);
			//column_id_header.setMaxWidth(0);
			column_id_header.setPreferredWidth(0);
			column_id_header.setMinWidth(0);
		}
	}

}
