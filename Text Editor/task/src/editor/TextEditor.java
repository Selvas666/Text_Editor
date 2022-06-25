package editor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    JTextField searchTextArea;
    JTextArea textArea;
    JFileChooser jfc;
    JCheckBox regexpBox;
    ArrayList<MatchResult> searchResults;
    ListIterator<MatchResult> iterator;
    int prevIndex;
    int maxIndex;
    MatchResult currentResult;

    public TextEditor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
//        setLayout(null);
        setTitle("Text editor by KALA");
        initializeComponents();
        setVisible(true);
//        revalidate();
//        repaint();
    }

    private void initializeComponents() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");

        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.setName("MenuOpen");
        loadItem.addActionListener(e -> loadFile());

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> this.saveFile());
        saveItem.setName("MenuSave");

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setName("MenuExit");
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");

        JMenuItem startSearchItem = new JMenuItem("Start search");
        startSearchItem.setName("MenuStartSearch");
        startSearchItem.addActionListener(e -> search());

        JMenuItem prevMatchItem = new JMenuItem("Previous match");
        prevMatchItem.setName("MenuPreviousMatch");
        prevMatchItem.addActionListener(e -> prev());

        JMenuItem nextMatchItem = new JMenuItem("Next match");
        nextMatchItem.setName("MenuNextMatch");
        nextMatchItem.addActionListener(e -> next());

        JMenuItem regExpItem = new JMenuItem("Use regExp");
        regExpItem.setName("MenuUseRegExp");
        regExpItem.addActionListener(e -> regexpBox.setSelected(!regexpBox.isSelected()));

        searchMenu.add(startSearchItem);
        searchMenu.add(prevMatchItem);
        searchMenu.add(nextMatchItem);
        searchMenu.add(regExpItem);

        menuBar.add(fileMenu);
        menuBar.add(searchMenu);

        this.searchTextArea = new JTextField();
        searchTextArea.setName("SearchField");
        searchTextArea.setVisible(true);
        searchTextArea.setPreferredSize(new Dimension(150, 20));


        ImageIcon saveIcon = new ImageIcon("C:\\Users\\tomga\\IdeaProjects\\Text Editor\\Text Editor\\task\\src\\editor\\resrc\\load2-download-icon.png");
        Image image = saveIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        saveIcon = new ImageIcon(newimg);  // transform it back
        JButton saveButton = new JButton(saveIcon);
        saveButton.addActionListener(e -> saveFile());
        saveButton.setName("SaveButton");

        ImageIcon loadIcon = new ImageIcon("C:\\Users\\tomga\\IdeaProjects\\Text Editor\\Text Editor\\task\\src\\editor\\resrc\\load2-upload-icon.png");
        image = loadIcon.getImage(); // transform it
        newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        loadIcon = new ImageIcon(newimg);  // transform it back
        JButton loadButton = new JButton(loadIcon);
        loadButton.addActionListener(e -> {
            loadFile();
            System.out.printf(loadButton.getName());
        });
        loadButton.setName("OpenButton");

        ImageIcon searchIcon = new ImageIcon("C:\\Users\\tomga\\IdeaProjects\\Text Editor\\Text Editor\\task\\src\\editor\\resrc\\Search-icon.png");
        image = searchIcon.getImage(); // transform it
        newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        searchIcon = new ImageIcon(newimg);  // transform it back
        JButton searchButton = new JButton(searchIcon);
        searchButton.addActionListener(e -> {
            try {
                search();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        searchButton.setName("StartSearchButton");

        ImageIcon prevIcon = new ImageIcon("C:\\Users\\tomga\\IdeaProjects\\Text Editor\\Text Editor\\task\\src\\editor\\resrc\\Actions-go-previous-icon.png");
        image = prevIcon.getImage(); // transform it
        newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        prevIcon = new ImageIcon(newimg);  // transform it back
        JButton prevButton = new JButton(prevIcon);
        prevButton.addActionListener(e -> prev());
        prevButton.setName("PreviousMatchButton");

        ImageIcon nextIcon = new ImageIcon("C:\\Users\\tomga\\IdeaProjects\\Text Editor\\Text Editor\\task\\src\\editor\\resrc\\Actions-go-next-icon.png");
        image = nextIcon.getImage(); // transform it
        newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        nextIcon = new ImageIcon(newimg);  // transform it back
        JButton nextButton = new JButton(nextIcon);
        nextButton.addActionListener(e -> next());
        nextButton.setName("NextMatchButton");

        regexpBox = new JCheckBox("use regExp");
        regexpBox.setSelected(false);
        regexpBox.setName("UseRegExCheckbox");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(loadButton);
        topPanel.add(saveButton);
        topPanel.add(searchTextArea);
        topPanel.add(regexpBox);
        topPanel.add(searchButton);
        topPanel.add(prevButton);
        topPanel.add(nextButton);

        JPanel mainTopPanel = new JPanel(new BorderLayout());
        mainTopPanel.add(menuBar, BorderLayout.NORTH);
        mainTopPanel.add(topPanel, BorderLayout.SOUTH);


        add(mainTopPanel, BorderLayout.NORTH);

        this.jfc = new JFileChooser();
        jfc.setName("FileChooser");
        jfc.setVisible(false);
        add(jfc, BorderLayout.CENTER);

        this.textArea = new JTextArea();
        textArea.setName("TextArea");
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(300, 300));
        scroll.setName("ScrollPane");
        add(scroll);


    }


    private void saveFile() {

        this.jfc.setVisible(true);
        int returnVal = jfc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.jfc.setVisible(false);
            File file = jfc.getSelectedFile();
            try (PrintWriter printWriter = new PrintWriter(file)) {

                printWriter.print(textArea.getText());

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void loadFile() {
        String file;
        this.jfc.setVisible(true);

        try {
            int returnVal = this.jfc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.jfc.setVisible(false);
                file = this.jfc.getSelectedFile().getAbsolutePath();
                String txt = new String(Files.readAllBytes(Paths.get(file)));
                this.textArea.setText(txt);
            }
        } catch (Exception e) {
            this.textArea.setText("");
            System.out.println(e.getMessage());
        }

    }

    private void search() {

        String src = textArea.getText();
        String pattern = searchTextArea.getText();
        Boolean regexp = regexpBox.isSelected();

        searchResults = new Searcher(src, pattern, regexp).doInBackground();
        if (searchResults != null) {
            iterator = searchResults.listIterator();
            maxIndex = searchResults.size() - 1;
            prevIndex = -1;
            next();
        }

    }

    private void grab(int index, int length) {
        textArea.setCaretPosition(index + length);
        textArea.select(index, index + length);
        textArea.grabFocus();
    }

    private void next() {
        if (iterator != null) {
            if (iterator.nextIndex() == 0 && prevIndex == 0) {
                iterator.next();
                prevIndex = iterator.nextIndex();
                currentResult = iterator.next();
                grab(currentResult.start(), currentResult.group().length());
            } else if (iterator.hasNext() && !(iterator.nextIndex() == maxIndex && prevIndex == maxIndex)) {
                prevIndex = iterator.nextIndex();
                currentResult = iterator.next();
                grab(currentResult.start(), currentResult.group().length());
            } else {
                iterator = searchResults.listIterator();
                prevIndex = iterator.nextIndex();
                currentResult = iterator.next();
                grab(currentResult.start(), currentResult.group().length());
            }

        }
    }

    private void prev() {
        if (iterator != null) {
            if (iterator.previousIndex() == maxIndex && prevIndex == maxIndex) {
                iterator.previous();
                prevIndex = iterator.previousIndex();
                currentResult = iterator.previous();
                grab(currentResult.start(), currentResult.group().length());
            } else if (iterator.hasPrevious() && !(iterator.previousIndex() == 0 && prevIndex == 0)) {
                prevIndex = iterator.previousIndex();
                currentResult = iterator.previous();
                grab(currentResult.start(), currentResult.group().length());
            } else {
                while (iterator.hasNext()) iterator.next();
                prevIndex = iterator.previousIndex();
                currentResult = iterator.previous();
                grab(currentResult.start(), currentResult.group().length());
            }
        }
    }

}

class Searcher extends SwingWorker<ArrayList<MatchResult>, ArrayList<MatchResult>> {

    String src;
    String searched;
    Boolean regexp;
    ArrayList<MatchResult> result;

    Searcher(String src, String searched, Boolean regexp) {
        this.searched = searched;
        this.src = src;
        this.regexp = regexp;
    }

    @Override
    protected ArrayList<MatchResult> doInBackground() {

        if (src.length() > 0 && searched.length() > 0) {
            Pattern pattern;
            result = new ArrayList<>();

            if (regexp) {
                pattern = Pattern.compile(searched);
            } else {
                pattern = Pattern.compile(searched, Pattern.LITERAL);
            }
            Matcher matcher = pattern.matcher(src);

            while (matcher.find()) result.add(matcher.toMatchResult());

            return result;
        }
        return null;
    }
}
