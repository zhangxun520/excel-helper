package com.zhangxun;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.zhangxun.biz.CreateExcel;
import com.zhangxun.biz.ReloveData;
import com.zhangxun.component.ColumnSetting;
import com.zhangxun.model.TargetData;
import com.zhangxun.util.Money;
import com.zhangxun.util.StringUtil;

public class Main extends JFrame implements ActionListener {
	private static final long serialVersionUID = -5473788932682692935L;

	private JButton chooseFileButton;
	private JButton columnSettingButton;
	private JTextArea chooseFileText;
	private JButton saveFileButton;
	private JTextArea saveFileText;
	private JTable table;
	private DefaultTableModel myTableModel;

	private JButton saveButton;
	private JButton closeButton;

	private JTextArea totalCountText;
	private JTextArea totalInText;
	private JTextArea totalOutText;
	private JTextArea totalText;

	public static String[] columnNames = { "凭证日期", "会计年度", "会计期间", "凭证字", "凭证号", "科目代码", "科目名称", "币别代码", "币别名称", "原币金额",
			"借方", "贷方", "制单", "审核", "核准", "出纳", "经办", "结算方式", "结算号", "凭证摘要", "数量", "数量单位", "单价", "参考信息", "业务日期",
			"往来业务编号", "附件数", "序号", "系统模块", "业务描述", "汇率类型", "汇率", "分录序号", "核算项目", "过账", "机制凭证", "现金流量" };

	private static final String FILE_NAME = "凭证";

	public Map<String, JFrame> frames = new HashMap<>();

	public static void main(String[] args) {
		new Main();
	}

	/**
	 * 初始化
	 */
	public Main() {
		setSize(1200, 620);
		setLocationRelativeTo(null);
		setBackground(Color.WHITE);
		setTitle("EXCEL--工具");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		initComponent();
		setVisible(true);
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		JMenuBar jMenuBar = new JMenuBar();
		JMenu initMenu = new JMenu("初始化"), settingMenu = new JMenu("设置");
		jMenuBar.add(initMenu);
		jMenuBar.add(settingMenu);
		// setJMenuBar(jMenuBar);

		JLabel label = new JLabel("源文件:");
		label.setBounds(10, 20, 60, 30);
		add(label);

		chooseFileText = new JTextArea(" 选择文件");
		chooseFileText.setBounds(80, 25, 250, 20);
		chooseFileText.setEditable(false);
		chooseFileText.setDisabledTextColor(Color.GRAY);
		chooseFileText.setBorder(new LineBorder(new java.awt.Color(127, 157, 185), 1, false));
		add(chooseFileText);

		chooseFileButton = new JButton("选择文件");
		chooseFileButton.setBounds(340, 20, 100, 30);
		chooseFileButton.addActionListener(this);
		add(chooseFileButton);

		columnSettingButton = new JButton("列显示设置");
		columnSettingButton.setBounds(460, 20, 100, 30);
		columnSettingButton.addActionListener(this);
		add(columnSettingButton);

		JScrollPane scrollPane = new JScrollPane(); // 支持滚动
		scrollPane.setBounds(10, 60, 1160, 400);
		add(scrollPane);

		table = new JTable(); // 自定义的表格

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // 关闭表格列的自动调整功能。
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选
		table.setSelectionBackground(Color.YELLOW);
		table.setSelectionForeground(Color.RED);
		table.setRowHeight(30);

		myTableModel = new DefaultTableModel(columnNames, 0);
		table.setModel(myTableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for (int i = 0; i < columnNames.length; i++) {
			if (!ColumnSetting.getDefaultIntegers().contains(i)) {
				TableColumn tableColumn = table.getColumnModel().getColumn(i);
				TableColumn column_id_header = table.getTableHeader().getColumnModel().getColumn(i);
				tableColumn.setMinWidth(0);
				tableColumn.setPreferredWidth(0);
				tableColumn.setMaxWidth(0);
				column_id_header.setMaxWidth(0);
				column_id_header.setPreferredWidth(0);
				column_id_header.setMinWidth(0);
			}
		}

		scrollPane.setViewportView(table); // 支持滚动

		JLabel label3 = new JLabel("总条数:");
		label3.setBounds(10, 475, 60, 30);
		add(label3);

		totalCountText = new JTextArea("");
		totalCountText.setBounds(80, 480, 100, 20);
		totalCountText.setBorder(new LineBorder(new java.awt.Color(127, 157, 185), 1, false));
		totalCountText.setDisabledTextColor(Color.GRAY);
		add(totalCountText);
		JLabel label4 = new JLabel("借方总计:");
		label4.setBounds(190, 475, 60, 30);
		add(label4);

		totalOutText = new JTextArea("");
		totalOutText.setBounds(260, 480, 100, 20);
		totalOutText.setBorder(new LineBorder(new java.awt.Color(127, 157, 185), 1, false));
		totalOutText.setDisabledTextColor(Color.GRAY);
		add(totalOutText);

		JLabel label5 = new JLabel("贷方总计:");
		label5.setBounds(370, 475, 60, 30);
		add(label5);

		totalInText = new JTextArea("");
		totalInText.setBounds(440, 480, 100, 20);
		totalInText.setBorder(new LineBorder(new java.awt.Color(127, 157, 185), 1, false));
		totalInText.setDisabledTextColor(Color.GRAY);
		add(totalInText);

		JLabel label6 = new JLabel("总计:");
		label6.setBounds(580, 475, 30, 30);
		add(label6);

		totalText = new JTextArea("");
		totalText.setBounds(620, 480, 100, 20);
		totalText.setBorder(new LineBorder(new java.awt.Color(127, 157, 185), 1, false));
		totalText.setDisabledTextColor(Color.GRAY);
		add(totalText);

		JLabel label2 = new JLabel("保存路径:");
		label2.setBounds(10, 510, 60, 30);
		add(label2);

		saveFileText = new JTextArea("");
		saveFileText.setBounds(80, 515, 300, 20);
		saveFileText.setBorder(new LineBorder(new java.awt.Color(127, 157, 185), 1, false));
		saveFileText.setDisabledTextColor(Color.GRAY);
		add(saveFileText);

		saveFileButton = new JButton("选择保存路径");
		saveFileButton.setBounds(390, 510, 120, 30);
		saveFileButton.addActionListener(this);
		add(saveFileButton);

		saveButton = new JButton("保存");
		saveButton.setBounds(30, 550, 80, 30);
		saveButton.addActionListener(this);
		add(saveButton);

		closeButton = new JButton("关闭");
		closeButton.setBounds(150, 550, 80, 30);
		closeButton.addActionListener(this);
		add(closeButton);
	}

	/**
	 * 事件监听
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chooseFileButton) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(0);
			int status = chooser.showOpenDialog(null);
			if (status == 1) {
				return;
			} else {
				// 读取选择器选择到的文件
				File file = chooser.getSelectedFile();
				if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
					// 获取文件绝对路径并写入到文本框内
					chooseFileText.setText(file.getAbsolutePath());

					setTableData(file);
				} else {
					JOptionPane.showMessageDialog(this, "不是EXCEL文件", "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == columnSettingButton) {
			if (frames.get(ColumnSetting.class.getName()) == null) {
				frames.put(ColumnSetting.class.getName(), new ColumnSetting(this));
			} else {
				frames.get(ColumnSetting.class.getName()).setVisible(true);
			}
		} else if (e.getSource() == saveFileButton) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(1);
			int status = chooser.showOpenDialog(null);
			if (status == 1) {
				return;
			} else {
				// 读取选择器选择到的文件
				File file = chooser.getSelectedFile();
				String dateString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				String fileName = file.getPath() + "\\" + FILE_NAME + "_" + dateString + ".xls";
				saveFileText.setText(fileName);
			}
		} else if (e.getSource() == saveButton) {
			if (StringUtil.isEmpty(saveFileText.getText())) {
				JOptionPane.showMessageDialog(this, "选择保存位置", "错误", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (myTableModel.getRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "无可保存数据", "错误", JOptionPane.WARNING_MESSAGE);
				return;
			}

			Money totalIn = new Money(totalInText.getText());
			Money totalOut = new Money(totalOutText.getText());
			Money total = new Money(totalText.getText());

			if (!totalIn.equals(totalOut)) {
				if (JOptionPane.showConfirmDialog(this, "出入金不匹配,是否保存?", "警告", JOptionPane.YES_NO_OPTION) != 0) {
					return;
				}
			}

			if (!totalIn.addTo(totalOut).equals(total)) {
				if (JOptionPane.showConfirmDialog(this, "出入金与合计不匹配,是否保存?", "警告", JOptionPane.YES_NO_OPTION) != 0) {
					return;
				}
			}

			Object[][] object = new Object[myTableModel.getRowCount()][columnNames.length];

			for (int i = 0; i < myTableModel.getRowCount(); i++) {
				for (int j = 0; j < columnNames.length; j++) {
					object[i][j] = myTableModel.getValueAt(i, j);
				}
			}
			if (CreateExcel.saveData(saveFileText.getText(), columnNames, object)) {
				JOptionPane.showMessageDialog(this, "保存成功", "完成", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "保存失败", "错误", JOptionPane.WARNING_MESSAGE);
			}
		} else if (e.getSource() == closeButton) {
			System.exit(0);
		}
	}

	/**
	 * 设置数据
	 * 
	 * @param file
	 */
	private void setTableData(File file) {
		try {
			List<TargetData> datas = ReloveData.relove(file);
			if (datas == null) {
				JOptionPane.showMessageDialog(this, "没有可处理数据", "错误", JOptionPane.WARNING_MESSAGE);
			}
			Object[][] vData = new Object[datas.size()][columnNames.length];

			Money inCount = new Money(0);
			Money outCount = new Money(0);
			Money totalCount = new Money(0);

			while (myTableModel.getRowCount() > 0) {
				myTableModel.removeRow(myTableModel.getRowCount() - 1);
			}

			ArrayList<Integer> warnIndex = new ArrayList<>();

			for (int i = 0; i < datas.size(); i++) {
				vData[i] = datas.get(i).getRow(columnNames.length);
				inCount.addTo(datas.get(i).getInAmount());
				outCount.addTo(datas.get(i).getOutAmount());
				totalCount.addTo(datas.get(i).getTotalAmount());
				myTableModel.addRow(vData[i]);
				if (datas.get(i).isWarn()) {
					warnIndex.add(i);
				}
			}
			setRowBackgroundColor(table, warnIndex, Color.RED);

			totalCountText.setText(String.valueOf(datas.size()));
			totalInText.setText(inCount.toString());
			totalOutText.setText(outCount.toString());
			totalText.setText(totalCount.toString());

			table.getModel().addTableModelListener(new TableModelListener() {

				@Override
				public void tableChanged(TableModelEvent e) {
					if (e.getType() == TableModelEvent.UPDATE) {
						if (e.getColumn() == 11 || e.getColumn() == 10 || e.getColumn() == 9) {
							Money sum = new Money();
							for (int i = 0; i < table.getRowCount(); i++) {
								Object data = myTableModel.getValueAt(i, e.getColumn());
								if (data.getClass() == Money.class) {
									sum.addTo((Money) data);
								} else if (data.getClass() == String.class) {
									sum.addTo(new Money((String) data));
								} else if (data.getClass() == Double.class) {
									sum.addTo(new Money((Double) data));
								}
							}

							if (e.getColumn() == 11) {
								totalInText.setText(sum.toString());
							} else if (e.getColumn() == 10) {
								totalOutText.setText(sum.toString());
							} else if (e.getColumn() == 9) {
								totalText.setText(sum.toString());
							}

						}
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置表格的某一行的背景色
	 * 
	 * @param table
	 */
	public static void setRowBackgroundColor(JTable table, ArrayList<Integer> rowIndexs, Color color) {
		try {
			DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					if (rowIndexs.contains(row)) {
						setBackground(color);
						setForeground(Color.WHITE);
					} else {
						setBackground(Color.WHITE);
						setForeground(Color.BLACK);
					}
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			};
			int columnCount = table.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public JTable getTable() {
		return table;
	}
}
