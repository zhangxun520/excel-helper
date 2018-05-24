package com.zhangxun;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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

public class Main extends JFrame implements ActionListener {
	private static final long serialVersionUID = -5473788932682692935L;

	private JButton chooseFileButton;
	private JButton columnSettingButton;
	private JButton addButton;
	private JButton copyButton;
	private JButton removeButton;
	private JTextArea chooseFileText;
	private JTable table;
	private DefaultTableModel myTableModel;

	private JButton saveButton;
	private JButton closeButton;

	private JLabel totalCountText;
	private JTextArea disparityText;
	private JTextArea totalDisparityText;
	private Money inAmount = new Money(0);
	private Money outAmount = new Money(0);
	private Money totalAmount = new Money(0);

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
		setMinimumSize(new Dimension(1200, 620));
		setLocationRelativeTo(null);
		setBackground(Color.WHITE);
		setTitle("EXCEL--工具");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// setLayout(null);
		// setResizable(false);
		initComponent();
		setVisible(true);
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		JPanel topJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("源文件:");
		label.setBounds(10, 20, 60, 30);
		topJPanel.add(label);

		chooseFileText = new JTextArea(" 选择文件                               ");
		chooseFileText.setEditable(false);
		chooseFileText.setDisabledTextColor(Color.GRAY);
		chooseFileText.setBorder(new LineBorder(new Color(127, 157, 185), 1, false));
		topJPanel.add(chooseFileText);

		chooseFileButton = new JButton("选择文件");
		chooseFileButton.addActionListener(this);
		topJPanel.add(chooseFileButton);

		columnSettingButton = new JButton("列显示设置");
		columnSettingButton.addActionListener(this);
		topJPanel.add(columnSettingButton);

		addButton = new JButton("添加");
		addButton.addActionListener(this);
		topJPanel.add(addButton);

		copyButton = new JButton("复制添加");
		copyButton.addActionListener(this);
		topJPanel.add(copyButton);

		removeButton = new JButton("移除");
		removeButton.addActionListener(this);
		topJPanel.add(removeButton);

		add(topJPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(); // 支持滚动
		scrollPane.setBounds(10, 60, 1160, 400);
		add(scrollPane, BorderLayout.CENTER);

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
			if (!ColumnSetting.getDefaultIntegers().contains(columnNames[i])) {
				TableColumn tableColumn = table.getColumnModel().getColumn(i);
				TableColumn column_id_header = table.getTableHeader().getColumnModel().getColumn(i);
				tableColumn.setMinWidth(0);
				tableColumn.setPreferredWidth(0);
				//tableColumn.setMaxWidth(0);
				//column_id_header.setMaxWidth(0);
				column_id_header.setPreferredWidth(0);
				column_id_header.setMinWidth(0);
			}
		}

		scrollPane.setViewportView(table); // 支持滚动

		JPanel footJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel label3 = new JLabel("总条数:");
		footJPanel.add(label3);

		totalCountText = new JLabel("0");
		footJPanel.add(totalCountText);

		JLabel label4 = new JLabel("     ");
		label4.setBounds(190, 475, 60, 30);
		footJPanel.add(label4);

		JLabel label5 = new JLabel("借-贷差值:");
		footJPanel.add(label5);

		disparityText = new JTextArea("0");
		disparityText.setBorder(new LineBorder(new Color(127, 157, 185), 1, false));
		disparityText.setDisabledTextColor(Color.GRAY);
		footJPanel.add(disparityText);

		JLabel label6 = new JLabel("     ");
		footJPanel.add(label6);
		JLabel label7 = new JLabel("总-(借贷)差值:");
		footJPanel.add(label7);

		totalDisparityText = new JTextArea("0");
		totalDisparityText.setBorder(new LineBorder(new Color(127, 157, 185), 1, false));
		totalDisparityText.setDisabledTextColor(Color.GRAY);
		footJPanel.add(totalDisparityText);

		JLabel label8 = new JLabel("            ");
		footJPanel.add(label8);

		saveButton = new JButton("保存");
		saveButton.addActionListener(this);
		footJPanel.add(saveButton);

		closeButton = new JButton("关闭");
		closeButton.addActionListener(this);
		footJPanel.add(closeButton);
		add(footJPanel, BorderLayout.SOUTH);
	}

	/**
	 * 事件监听
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chooseFileButton) {// 选择源文件
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
		} else if (e.getSource() == columnSettingButton) {// 列选项
			if (frames.get(ColumnSetting.class.getName()) == null) {
				frames.put(ColumnSetting.class.getName(), new ColumnSetting(this));
			} else {
				frames.get(ColumnSetting.class.getName()).setVisible(true);
			}
		} else if (e.getSource() == addButton) {// 添加空行
			myTableModel.addRow(new Object[columnNames.length]);
			totalCountText.setText(myTableModel.getRowCount() + "");
		} else if (e.getSource() == copyButton) {// 复制添加行
			int row = table.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(Main.this, "请选择要复制的行!");
			} else {
				Object[] objects = new Object[columnNames.length];
				for (int i = 0; i < columnNames.length; i++) {
					objects[i] = myTableModel.getValueAt(row, i);
				}
				myTableModel.addRow(objects);
				totalCountText.setText(myTableModel.getRowCount() + "");
			}
		} else if (e.getSource() == removeButton) {// 移除
			int row = table.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(Main.this, "请选择要删除的行!");
			} else {
				myTableModel.removeRow(row);
				totalCountText.setText(myTableModel.getRowCount() + "");
			}
		} else if (e.getSource() == saveButton) {
			if (myTableModel.getRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "无可保存数据", "错误", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (!inAmount.equals(outAmount)) {
				if (JOptionPane.showConfirmDialog(this, "出入金不匹配,是否保存?", "警告", JOptionPane.YES_NO_OPTION) != 0) {
					return;
				}
			}
			if (!inAmount.add(outAmount).equals(totalAmount)) {
				if (JOptionPane.showConfirmDialog(this, "出入金与总金额不匹配,是否保存?", "警告", JOptionPane.YES_NO_OPTION) != 0) {
					return;
				}
			}

			// 读取选择器选择到的文件
			String dateString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new File(FILE_NAME + "_" + dateString + ".xls"));
			int status = chooser.showSaveDialog(Main.this);
			if (status == 1) {
				return;
			}

			Object[][] object = new Object[myTableModel.getRowCount()][columnNames.length];

			for (int i = 0; i < myTableModel.getRowCount(); i++) {
				for (int j = 0; j < columnNames.length; j++) {
					object[i][j] = myTableModel.getValueAt(i, myTableModel.findColumn(columnNames[j]));
				}
			}
			if (CreateExcel.saveData(chooser.getSelectedFile().getPath(), columnNames, object)) {
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

			while (myTableModel.getRowCount() > 0) {
				myTableModel.removeRow(myTableModel.getRowCount() - 1);
			}

			resetAmount();
			ArrayList<Integer> warnIndex = new ArrayList<>();
			for (int i = 0; i < datas.size(); i++) {
				vData[i] = datas.get(i).getRow(columnNames.length);

				inAmount.addTo(datas.get(i).getInAmount());
				outAmount.addTo(datas.get(i).getOutAmount());
				totalAmount.addTo(datas.get(i).getTotalAmount());
				myTableModel.addRow(vData[i]);
				if (datas.get(i).isWarn()) {
					warnIndex.add(i);
				}
			}
			setRowBackgroundColor(table, warnIndex, Color.RED);

			totalCountText.setText(String.valueOf(datas.size()));
			setDisparityAmount();
			table.getModel().addTableModelListener(new TableModelListener() {

				@Override
				public void tableChanged(TableModelEvent e) {
					if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.UPDATE
							|| e.getType() == TableModelEvent.DELETE) {
						resetAmount();
						for (int i = 0; i < table.getRowCount(); i++) {
							for (int j = 9; j <= 11; j++) {
								Money money = getMoney(myTableModel.getValueAt(i, j));
								if (j == 11) {
									inAmount.addTo(money);
								} else if (j == 10) {
									outAmount.addTo(money);
								} else if (j == 9) {
									totalAmount.addTo(money);
								}
							}

						}
						setDisparityAmount();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Money getMoney(Object data) {
		if (data == null) {
			return new Money(0);
		}
		if (data.getClass() == Money.class) {
			return (Money) data;
		} else if (data.getClass() == String.class) {
			return new Money((String) data);
		} else if (data.getClass() == Double.class) {
			return new Money((Double) data);
		} else {
			return new Money(0);
		}
	}

	private void resetAmount() {
		inAmount.setCent(0);
		outAmount.setCent(0);
		totalAmount.setCent(0);
	}

	private void setDisparityAmount() {
		disparityText.setText(outAmount.subtract(inAmount).toString());
		totalDisparityText.setText(totalAmount.subtract(outAmount).subtract(inAmount).toString());
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
