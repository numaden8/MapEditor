import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * Created on 2006/12/02
 */

public class EventDialog extends JDialog {
    private static final int DOWN = 3;

    // �C�x���g���W
    private int x, y;
    // �C�x���g�̃}�b�v�`�b�v�ԍ�
    private int mapchipNo = 128;
    // �L�����N�^�[�ԍ�
    private int charaNo = 0;

    // �������ꂽ�C���[�W
    private Image[] mapchipImages;
    private Image[][] charaImages;

    // �C�x���g���X�g
    private ArrayList eventList = new ArrayList();

    // GUI���i
    private JLabel mapchipLabel;
    private JLabel coordLabel1;
    private JTextField destMapNoText;
    private JTextField destXText;
    private JTextField destYText;
    private JButton okButton;
    private JButton cancelButton;

    private JLabel charaLabel;
    private JLabel coordLabel2;
    private JComboBox directionBox;
    private JComboBox moveTypeBox;
    private JTextArea messageArea;

    private JLabel treasureLabel;
    private JLabel coordLabel3;
    private JTextField itemText;

    private JLabel doorLabel;
    private JLabel coordLabel4;

    // �p���b�g�_�C�A���O�ւ̎Q��
    private PaletteDialog paletteDialog;
    // �L�����N�^�[�_�C�A���O�ւ̎Q��
    private CharaDialog charaDialog;
    // ���C���p�l���ւ̎Q��
    private MainPanel mainPanel;

    public EventDialog(JFrame parent, PaletteDialog paletteDialog,
            CharaDialog charaDialog) {
        super(parent, "�C�x���g�_�C�A���O", false);
        this.paletteDialog = paletteDialog;
        this.charaDialog = charaDialog;

        // �������ꂽ�}�b�v�`�b�v�C���[�W���p���b�g�_�C�A���O����擾
        mapchipImages = paletteDialog.getMapchipImages();
        charaImages = charaDialog.getCharaImages();

        // GUI��������
        initGUI();
        pack();
    }

    /**
     * GUI������������
     */
    private void initGUI() {
        // �^�u�y�C��
        JTabbedPane tabbedPane = new JTabbedPane();

        // �ړ��C�x���g�^�u�̃p�l��
        JPanel moveEventPanel = new JPanel();
        moveEventPanel.setLayout(new BorderLayout());

        JPanel p1 = new JPanel();
        // �}�b�v�`�b�v���x��
        mapchipLabel = new JLabel();
        mapchipLabel.setIcon(new ImageIcon(mapchipImages[mapchipNo]));
        mapchipLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // �I������Ă���}�b�v�`�b�v�����x���ɃZ�b�g����
                mapchipNo = paletteDialog.getSelectedMapchipNo();
                mapchipLabel.setIcon(new ImageIcon(mapchipImages[mapchipNo]));
            }
        });
        p1.add(mapchipLabel);

        // ���W���x��
        JPanel p2 = new JPanel();
        coordLabel1 = new JLabel("(0, 0)");
        p2.add(coordLabel1);

        JPanel p3 = new JPanel();
        p3.setLayout(new GridLayout(2, 1));
        p3.add(p1);
        p3.add(p2);

        JPanel p4 = new JPanel();
        p4.setLayout(new GridLayout(5, 2));

        // �ړ���}�b�v�ԍ�
        destMapNoText = new JTextField(3);
        p4.add(new JLabel("�ړ���}�b�v�ԍ�"));
        p4.add(destMapNoText);

        // �ړ�����W
        destXText = new JTextField(3);
        destYText = new JTextField(3);
        p4.add(new JLabel("�ړ���X"));
        p4.add(destXText);
        p4.add(new JLabel("�ړ���Y"));
        p4.add(destYText);

        JPanel p5 = new JPanel();
        p5.setLayout(new GridLayout(1, 2));
        // OK�A�L�����Z���{�^��
        okButton = new JButton("OK");
        cancelButton = new JButton("��ݾ�");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // �C�x���g�I�u�W�F�N�g���쐬
                MoveEvent evt;
                try {
                    evt = new MoveEvent(x, y, mapchipNo, Integer
                            .parseInt(destMapNoText.getText()), Integer
                            .parseInt(destXText.getText()), Integer
                            .parseInt(destYText.getText()));
                } catch (NumberFormatException ex) {
                    // �e�L�X�g�{�b�N�X�ɐ��l�ȊO�����͂��ꂽ�Ƃ�
                    JOptionPane.showMessageDialog(EventDialog.this,
                            "���l����͂��Ă�������");
                    destMapNoText.setText("");
                    destXText.setText("");
                    destYText.setText("");
                    return;
                }
                // �C�x���g���X�g�ɒǉ�
                eventList.add(evt);
                setVisible(false);
                mainPanel.repaint();
                System.out.println("�ړ��C�x���g�ǉ�: " + evt);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        p5.add(okButton);
        p5.add(cancelButton);

        moveEventPanel.add(p3, BorderLayout.NORTH);
        moveEventPanel.add(p4, BorderLayout.CENTER);
        moveEventPanel.add(p5, BorderLayout.SOUTH);

        tabbedPane.addTab("�ړ�", moveEventPanel);

        // �L�����N�^�C�x���g�^�u���̃p�l��
        JPanel charaEventPanel = new JPanel();
        charaEventPanel.setLayout(new BorderLayout());

        JPanel p6 = new JPanel();
        charaLabel = new JLabel();
        charaLabel.setIcon(new ImageIcon(charaImages[0][DOWN]));
        charaLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // �I������Ă���L�����N�^�[�����x���ɃZ�b�g����
                charaNo = charaDialog.getSelectedCharaNo();
                charaLabel.setIcon(new ImageIcon(charaImages[charaNo][DOWN]));
            }
        });
        p6.add(charaLabel);

        // ���W���x��
        JPanel p7 = new JPanel();
        coordLabel2 = new JLabel("(0, 0)");
        p7.add(coordLabel2);

        JPanel p8 = new JPanel();
        p8.setLayout(new GridLayout(2, 1));
        p8.add(p6);
        p8.add(p7);

        JPanel p9 = new JPanel();
        p9.setLayout(new GridLayout(2, 2));

        // ����
        directionBox = new JComboBox();
        directionBox.setEditable(false);
        directionBox.addItem("������");
        directionBox.addItem("�E����");
        directionBox.addItem("�����");
        directionBox.addItem("������");
        directionBox.setSelectedIndex(DOWN); // �f�t�H���g�͉�����
        directionBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = directionBox.getSelectedIndex();
                // ���x���̊G�̌�����ς���
                charaLabel.setIcon(new ImageIcon(charaImages[charaNo][index]));
            }
        });
        p9.add(new JLabel("����"));
        p9.add(directionBox);

        // �ړ��^�C�v
        moveTypeBox = new JComboBox();
        moveTypeBox.setEditable(false);
        moveTypeBox.addItem("�ړ����Ȃ�");
        moveTypeBox.addItem("�����_���ړ�");
        p9.add(new JLabel("�ړ��^�C�v"));
        p9.add(moveTypeBox);

        // ���b�Z�[�W
        JPanel p10 = new JPanel();
        messageArea = new JTextArea();
        messageArea.setRows(5);
        messageArea.setColumns(18);
        messageArea.setLineWrap(true);
        messageArea.setText("�����Ƀ��b�Z�[�W����͂��Ă��������B���s��\\n�A���y�[�W��\\f");
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(messageArea);
        p10.add(scrollPane);

        // OK�A�L�����Z���{�^��
        JPanel p11 = new JPanel();
        p11.setLayout(new GridLayout(1, 2));
        okButton = new JButton("OK");
        cancelButton = new JButton("��ݾ�");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // �C�x���g�I�u�W�F�N�g���쐬
                CharaEvent evt = new CharaEvent(x, y, charaNo, directionBox
                        .getSelectedIndex(), moveTypeBox.getSelectedIndex(),
                        messageArea.getText());
                // �C�x���g���X�g�ɒǉ�
                eventList.add(evt);
                setVisible(false);
                mainPanel.repaint();
                System.out.println("�L�����N�^�[�C�x���g�ǉ�: " + evt);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        p11.add(okButton);
        p11.add(cancelButton);

        JPanel p12 = new JPanel();
        p12.setLayout(new BorderLayout());
        p12.add(p9, BorderLayout.NORTH);
        p12.add(p10, BorderLayout.CENTER);

        charaEventPanel.add(p8, BorderLayout.NORTH);
        charaEventPanel.add(p12, BorderLayout.CENTER);
        charaEventPanel.add(p11, BorderLayout.SOUTH);

        tabbedPane.add("�L����", charaEventPanel);

        // �󔠃C�x���g
        JPanel treasureEventPanel = new JPanel();
        treasureEventPanel.setLayout(new BorderLayout());

        JPanel p13 = new JPanel();
        // �}�b�v�`�b�v���x��
        treasureLabel = new JLabel();
        treasureLabel.setIcon(new ImageIcon(mapchipImages[194])); // �󔠂�194��
        p13.add(treasureLabel);

        // ���W���x��
        JPanel p14 = new JPanel();
        coordLabel3 = new JLabel("(0, 0)");
        p14.add(coordLabel3);

        // �A�C�e����
        JPanel p15 = new JPanel();
        itemText = new JTextField(16);
        p15.add(itemText);

        JPanel p16 = new JPanel();
        p16.setLayout(new GridLayout(6, 1));

        p16.add(p13);
        p16.add(p14);
        p16.add(p15);

        // OK�A�L�����Z���{�^��
        JPanel p17 = new JPanel();
        p17.setLayout(new GridLayout(1, 2));
        okButton = new JButton("OK");
        cancelButton = new JButton("��ݾ�");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // �C�x���g�I�u�W�F�N�g���쐬
                TreasureEvent evt = new TreasureEvent(x, y, itemText.getText());
                // �C�x���g���X�g�ɒǉ�
                eventList.add(evt);
                setVisible(false);
                mainPanel.repaint();
                System.out.println("�󔠃C�x���g�ǉ�: " + evt);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        p17.add(okButton);
        p17.add(cancelButton);

        treasureEventPanel.add(p16, BorderLayout.CENTER);
        treasureEventPanel.add(p17, BorderLayout.SOUTH);

        tabbedPane.addTab("��", treasureEventPanel);

        // �h�A�C�x���g
        JPanel doorEventPanel = new JPanel();
        doorEventPanel.setLayout(new BorderLayout());

        JPanel p18 = new JPanel();
        // �}�b�v�`�b�v���x��
        doorLabel = new JLabel();
        doorLabel.setIcon(new ImageIcon(mapchipImages[195])); // �h�A��195��
        p18.add(doorLabel);

        // ���W���x��
        JPanel p19 = new JPanel();
        coordLabel4 = new JLabel("(0, 0)");
        p19.add(coordLabel4);

        JPanel p20 = new JPanel();
        p20.setLayout(new GridLayout(6, 1));

        p20.add(p18);
        p20.add(p19);

        // OK�A�L�����Z���{�^��
        JPanel p21 = new JPanel();
        p21.setLayout(new GridLayout(1, 2));
        okButton = new JButton("OK");
        cancelButton = new JButton("��ݾ�");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // �C�x���g�I�u�W�F�N�g���쐬
                DoorEvent evt = new DoorEvent(x, y);
                // �C�x���g���X�g�ɒǉ�
                eventList.add(evt);
                setVisible(false);
                mainPanel.repaint();
                System.out.println("�h�A�C�x���g�ǉ�: " + evt);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        p21.add(okButton);
        p21.add(cancelButton);

        doorEventPanel.setLayout(new BorderLayout());
        doorEventPanel.add(p20, BorderLayout.CENTER);
        doorEventPanel.add(p21, BorderLayout.SOUTH);

        tabbedPane.addTab("�h�A", doorEventPanel);

        getContentPane().add(tabbedPane);
    }

    /**
     * �C�x���g���W���Z�b�g
     * 
     * @param x X���W
     * @param y Y���W
     */
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;

        coordLabel1.setText("(" + x + ", " + y + ")");
        coordLabel2.setText("(" + x + ", " + y + ")");
        coordLabel3.setText("(" + x + ", " + y + ")");
        coordLabel4.setText("(" + x + ", " + y + ")");
    }

    /**
     * �C�x���g���X�g��Ԃ�
     * 
     * @return �C�x���g���X�g
     */
    public ArrayList getEvents() {
        return eventList;
    }

    /**
     * �C�x���g���X�g���Z�b�g
     * 
     * @param eventList �C�x���g���X�g
     */
    public void setEvents(ArrayList eventList) {
        this.eventList = eventList;
    }

    /**
     * ���C���p�l���ւ̎Q�Ƃ��Z�b�g
     * 
     * @param mainPanel ���C���p�l��
     */
    public void setMainPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }
}
