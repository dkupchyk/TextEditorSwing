/**
 * Завдання: Текстовий редактор
 * @author - Купчик Дарина
 * 1 курс КНіТ
 */

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;

public class TextEditor extends JFrame{

    private JComboBox<String> fontSizeComboBox;
    private JComboBox<String> fontFamilyComboBox;
    private static final String[] FONT_SIZES = {"Size", "24", "26", "28", "30", "32", "34", "36", "38", "40"};
    private static final String[] FONT_LIST = {"Font", "Arial", "Calibri", "Cambria", "Courier New", "Comic Sans MS", "Dialog", "Georgia", "Helevetica", "Lucida Sans", "Monospaced", "Tahoma", "Verdana"};
    private JTextPane textPane = new JTextPane();
    private Color defaultColor = new Color(230, 224, 155);
    private Font defaultFont = new Font("Times New Roman", Font.PLAIN, 20);
    private JScrollPane scrollPane;
    private JPanel panel = new JPanel();
    private JFrame frame;
    private File file;
    private UndoManager undoManager = new UndoManager();
    enum UndoActionType {UNDO, REDO};

    public static void main(String[] args) {
       new TextEditor();
    }

    public TextEditor(){
        this.setFrameTitleWithExtn("Text editor by Kupchyk Daryna");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width-700)/2-(this.getSize().width)/2, (dim.height-700)/2-this.getSize().height/2);

        JPanel functionMenu = new JPanel(new GridLayout(3, 5, 7,7));
        functionMenu.setPreferredSize(new Dimension(100, 150));
        functionMenu.setBackground(defaultColor);
        textPane.setFont(defaultFont);
        scrollPane = new JScrollPane(textPane);
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Info");
        JMenuItem infoItem = new JMenuItem("Information");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem openItem = new JMenuItem("Open");
        JMenuItem saveItem = new JMenuItem("Save as");
        JMenuItem printItem = new JMenuItem("Print");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(printItem);
        fileMenu.add(exitItem);
        helpMenu.add(infoItem);

        //Функція для New
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textPane.setText("");
                setFrameTitleWithExtn("New");
            }
        });

        //Функція для Open
        openItem.addActionListener(new OpenFileListener());
        //Функція для Save
        saveItem.addActionListener(new SaveFileListener());
        //Функція для Print
        printItem.addActionListener(new PrintFileListener());
        //Функція для Exit
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        //Функція для Information
        infoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "This text editor was created by Kupchyk Daryna.");
            }
        });

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        fontSizeComboBox = new JComboBox<String>(FONT_SIZES);
        fontSizeComboBox.setEditable(false);
        fontSizeComboBox.addItemListener(new FontSizeItemListener());
        functionMenu.add(fontSizeComboBox);

        fontFamilyComboBox = new JComboBox<String>(FONT_LIST);
        fontFamilyComboBox.setEditable(false);
        fontFamilyComboBox.addItemListener(new FontFamilyItemListener());
        functionMenu.add(fontFamilyComboBox);

        JButton left = new JButton(new StyledEditorKit.AlignmentAction(textPane.getSelectedText(), 0));
        ImageIcon leftIcon = new ImageIcon("src/res/left-align.png");
        Image img = leftIcon.getImage() ;
        Image newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        leftIcon = new ImageIcon( newIcon );
        left.setIcon(leftIcon);
        left.setText("");
        functionMenu.add(left);

        JButton center = new JButton(new StyledEditorKit.AlignmentAction(textPane.getSelectedText(), 1));
        ImageIcon centerIcon = new ImageIcon("src/res/center-align.png");
        img = centerIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        centerIcon = new ImageIcon( newIcon );
        center.setIcon(centerIcon);
        center.setText("");
        functionMenu.add(center);

        JButton right = new JButton(new StyledEditorKit.AlignmentAction(textPane.getSelectedText(), 2));
        ImageIcon rightIcon = new ImageIcon("src/res/right-align.png");
        img = rightIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        rightIcon = new ImageIcon( newIcon );
        right.setIcon(rightIcon);
        right.setText("");
        functionMenu.add(right);

        JButton color = new JButton("");
        color.setMaximumSize(new Dimension(1,1));
        ImageIcon colorIcon = new ImageIcon("src/res/colors.png");
        img = colorIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        colorIcon = new ImageIcon( newIcon );
        color.setIcon(colorIcon);
        color.setText("");
        color.addActionListener(new ColorActionListener());
        functionMenu.add(color);

        JButton breakParagraph = new JButton(new DefaultEditorKit.InsertBreakAction());
        ImageIcon paragIcon = new ImageIcon("src/res/paragraph.png");
        img = paragIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        paragIcon = new ImageIcon( newIcon );
        breakParagraph.setIcon(paragIcon);
        breakParagraph.setText("");
        functionMenu.add(breakParagraph);

        JButton bold = new JButton(new StyledEditorKit.BoldAction());
        ImageIcon boldIcon = new ImageIcon("src/res/bold.png");
        img = boldIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        boldIcon = new ImageIcon( newIcon );
        bold.setIcon(boldIcon);
        bold.setText("");
        functionMenu.add(bold);

        JButton italic = new JButton(new StyledEditorKit.ItalicAction());
        ImageIcon italicIcon = new ImageIcon("src/res/italic.png");
        img = italicIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        italicIcon = new ImageIcon( newIcon );
        italic.setIcon(italicIcon);
        italic.setText("");
        functionMenu.add(italic);

        JButton underline = new JButton(new StyledEditorKit.UnderlineAction());
        ImageIcon underlineIcon = new ImageIcon("src/res/underline.png");
        img = underlineIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        underlineIcon = new ImageIcon( newIcon );
        underline.setIcon(underlineIcon);
        underline.setText("");
        functionMenu.add(underline);

        JButton cut = new JButton(new DefaultEditorKit.CutAction());
        ImageIcon cutIcon = new ImageIcon("src/res/scissors.png");
        img = cutIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        cutIcon = new ImageIcon( newIcon );
        cut.setIcon(cutIcon);
        cut.setText("");
        functionMenu.add(cut);

        JButton copy = new JButton(new DefaultEditorKit.CopyAction());
        ImageIcon copyIcon = new ImageIcon("src/res/save.png");
        img = copyIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        copyIcon = new ImageIcon( newIcon );
        copy.setIcon(copyIcon);
        copy.setText("");
        functionMenu.add(copy);

        JButton paste = new JButton(new DefaultEditorKit.PasteAction());
        ImageIcon pasteIcon = new ImageIcon("src/res/paste.png");
        img = pasteIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        pasteIcon = new ImageIcon( newIcon );
        paste.setIcon(pasteIcon);
        paste.setText("");
        functionMenu.add(paste);

        textPane.setDocument(getNewDocument());

        JButton undoButton = new JButton();
        undoButton.addActionListener(new UndoActionListener(UndoActionType.UNDO));
        ImageIcon undoIcon = new ImageIcon("src/res/undo.png");
        img = undoIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        undoIcon = new ImageIcon( newIcon );
        undoButton.setIcon(undoIcon);
        undoButton.setText("");
        functionMenu.add(undoButton);

        JButton redoButton = new JButton();
        redoButton.addActionListener(new UndoActionListener(UndoActionType.REDO));
        ImageIcon redoIcon = new ImageIcon("src/res/redo.png");
        img = redoIcon.getImage() ;
        newIcon = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH ) ;
        redoIcon = new ImageIcon( newIcon );
        redoButton.setIcon(redoIcon);
        redoButton.setText("");
        functionMenu.add(redoButton);

        top.add(menuBar);

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.PAGE_AXIS));
        menu.add(top);
        menu.add(functionMenu);

        add(menu, BorderLayout.NORTH);
        add(panel,  BorderLayout.CENTER);
        setPreferredSize(new Dimension(700, 700));
        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private class UndoActionListener implements ActionListener {
        private UndoActionType undoActionType;
        public UndoActionListener(UndoActionType type) {
            undoActionType = type;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (undoActionType) {
                case UNDO:
                    if (!undoManager.canUndo()) {
                        textPane.requestFocusInWindow();
                        return; // no edits to undo
                    }
                    undoManager.undo();
                    break;
                case REDO:
                    if (! undoManager.canRedo()) {
                        textPane.requestFocusInWindow();
                        return; // no edits to redo
                    }
                    undoManager.redo();
            }
            textPane.requestFocusInWindow();
        }
    }

    //внутрішній клас для вибору розміру шрифту
    private class FontSizeItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {

            if ((e.getStateChange() != ItemEvent.SELECTED) ||
                    (fontSizeComboBox.getSelectedIndex() == 0)) {
                return;
            }

            String fontSizeStr = (String) e.getItem();
            int newFontSize = 0;

            try {
                newFontSize = Integer.parseInt(fontSizeStr);
            } catch (NumberFormatException ex) {

                return;
            }
            fontSizeComboBox.setAction(new StyledEditorKit.FontSizeAction(fontSizeStr, newFontSize));
            fontSizeComboBox.setSelectedIndex(0);
        }
    }

    //внутрішній клас для вибору шрифту
    private class FontFamilyItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {

            if ((e.getStateChange() != ItemEvent.SELECTED) ||
                    (fontFamilyComboBox.getSelectedIndex() == 0)) {
                return;
            }
            String fontFamily = (String) e.getItem();
            fontFamilyComboBox.setAction(new StyledEditorKit.FontFamilyAction(fontFamily, fontFamily));
            fontFamilyComboBox.setSelectedIndex(0);
        }
    }

    //Клас для вибору кольору тексту
    private class ColorActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Color newColor =
                    JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
            if (newColor == null) {
                textPane.requestFocusInWindow();
                return;
            }
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, newColor);
            textPane.setCharacterAttributes(attr, false);
            textPane.requestFocusInWindow();
        }
    }

    private class OpenFileListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            file = chooseFile();
            if (file == null) { return; }
            readFile(file);
            setFrameTitleWithExtn(file.getName());
        }

        private File chooseFile() {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                return chooser.getSelectedFile();
            }
            else { return null; }
        }

        private void readFile(File file) {

            StyledDocument doc = null;
            try (InputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                 doc = (DefaultStyledDocument) ois.readObject();
            }
            catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(frame, "Input file was not found!");
                return;
            }
            catch (ClassNotFoundException | IOException ex) {
                throw new RuntimeException(ex);
            }
            textPane.setDocument(doc);
            doc.addUndoableEditListener(new UndoEditListener());
            applyFocusListenerToPictures(doc);
        }

        private void applyFocusListenerToPictures(StyledDocument doc) {

            ElementIterator iterator = new ElementIterator(doc);
            Element element;
            while ((element = iterator.next()) != null) {
                AttributeSet attrs = element.getAttributes();
            }
        }

    }

    private void setFrameTitleWithExtn(String titleExtn) {
        this.setTitle("" + titleExtn);
    }

    private class UndoEditListener implements UndoableEditListener {
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
        }
    }

    private class SaveFileListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            file = chooseFile();
            DefaultStyledDocument doc = (DefaultStyledDocument) getEditorDocument();
            try (OutputStream fos = new FileOutputStream(file);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(doc);
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            setFrameTitleWithExtn(file.getName());
        }

        private File chooseFile() {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                return chooser.getSelectedFile();
            }else { return null; }
        }
    }

    private StyledDocument getEditorDocument() {
        StyledDocument doc = (DefaultStyledDocument) textPane.getDocument();
        return doc;
    }

    private class PrintFileListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                textPane.setContentType("text/html");
                boolean done = textPane.print();
                if (done) {
                    JOptionPane.showMessageDialog(null, "Printing is done");
                }
            } catch (Exception pex) {
                JOptionPane.showMessageDialog(null, "Error");
                pex.printStackTrace();
            }
        }
    }

    private StyledDocument getNewDocument() {
        StyledDocument doc = new DefaultStyledDocument();
        doc.addUndoableEditListener(new UndoEditListener());
        return doc;
    }

    private Image scaleImage(Image image, int w, int h) {
        Image scaled = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return scaled;
    }

}