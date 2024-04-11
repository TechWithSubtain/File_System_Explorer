import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Operating_System extends JFrame {
    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private JButton createButton;
    private JButton deleteButton;
    private JButton saveButton;
    private JButton searchButton;
    private JButton copyButton;
    private JButton cutButton;
    private JButton pasteButton;
    private JButton renameButton;
    private JTextField fileNameField;
    private File clipboardFile;
    private boolean isCutOperation;
    private JLabel fileInfoLabel;

    public Operating_System() {
        setTitle("File System Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setCellRenderer(new FileListCellRenderer(fileList));
        fileList.addListSelectionListener(e -> {
            File selectedFile = fileList.getSelectedValue();
            if (selectedFile != null) {
                fileInfoLabel.setText("File Name: " + selectedFile.getName() + " | Type: " + getFileExtension(selectedFile));
            } else {
                fileInfoLabel.setText("");
            }
        });

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        fileNameField = new JTextField();
        createButton = new JButton("Create");
        deleteButton = new JButton("Delete");
        saveButton = new JButton("Save");
        searchButton = new JButton("Search");
        copyButton = new JButton("Copy");
        cutButton = new JButton("Cut");
        pasteButton = new JButton("Paste");
        renameButton = new JButton("Rename");

        createButton.addActionListener(e -> createFile());
        deleteButton.addActionListener(e -> deleteFile());
        saveButton.addActionListener(e -> saveFile());
        searchButton.addActionListener(e -> searchFile());
        copyButton.addActionListener(e -> copyFile());
        cutButton.addActionListener(e -> cutFile());
        pasteButton.addActionListener(e -> pasteFile());
        renameButton.addActionListener(e -> renameFile());

        buttonPanel.add(fileNameField);
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(cutButton);
        buttonPanel.add(pasteButton);
        buttonPanel.add(renameButton);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton sortByNameButton = new JButton("Sort by Name");
        sortByNameButton.addActionListener(e -> sortByFileName());
        JButton sortByTypeButton = new JButton("Sort by Type");
        sortByTypeButton.addActionListener(e -> sortByFileType());
        JButton sortByDateButton = new JButton("Sort by Date");
        sortByDateButton.addActionListener(e -> sortByFileDate());
        sortPanel.add(sortByNameButton);
        sortPanel.add(sortByTypeButton);
        sortPanel.add(sortByDateButton);

        fileInfoLabel = new JLabel();
        JPanel fileInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileInfoPanel.add(fileInfoLabel);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(sortPanel, BorderLayout.NORTH);
        add(fileInfoPanel, BorderLayout.WEST);

        updateFileListByName();

        scrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                int fontSize = fileList.getFont().getSize();
                if (notches < 0) {
                    fileList.setFont(new Font(fileList.getFont().getName(), Font.PLAIN, fontSize + 1));
                } else {
                    fileList.setFont(new Font(fileList.getFont().getName(), Font.PLAIN, fontSize - 1));
                }
            }
        });

        setVisible(true);
    }

    private void createFile() {
        String fileName = fileNameField.getText().trim();
        if (!fileName.isEmpty()) {
            File file = new File(fileName);
            try {
                if (file.createNewFile()) {
                    updateFileList();
                    JOptionPane.showMessageDialog(this, "File created successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "File already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error creating file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a file name.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFile() {
        File selectedFile = fileList.getSelectedValue();
        if (selectedFile != null) {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this file?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                if (selectedFile.delete()) {
                    updateFileList();
                    JOptionPane.showMessageDialog(this, "File deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFile() {
        File selectedFile = fileList.getSelectedValue();
        if (selectedFile != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save File");
            fileChooser.setSelectedFile(selectedFile);
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File destination = fileChooser.getSelectedFile();
                try {
                    Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "File saved successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file to save.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchFile() {
        String fileName = JOptionPane.showInputDialog(this, "Enter file name to search:");
        if (fileName != null && !fileName.trim().isEmpty()) {
            File searchFile = new File(fileName);
            if (searchFile.exists()) {
                JOptionPane.showMessageDialog(this, "File found.");
            } else {
                JOptionPane.showMessageDialog(this, "File not found.", "Error", JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a file name.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyFile() {
        File selectedFile = fileList.getSelectedValue();
        if (selectedFile != null) {
            clipboardFile = selectedFile;
            isCutOperation
            = false;
            JOptionPane.showMessageDialog(this, "File copied to clipboard.");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file to copy.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cutFile() {
        File selectedFile = fileList.getSelectedValue();
        if (selectedFile != null) {
            clipboardFile = selectedFile;
            isCutOperation = true;
            JOptionPane.showMessageDialog(this, "File cut to clipboard.");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file to cut.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pasteFile() {
        if (clipboardFile != null) {
            String destinationFileName = fileNameField.getText().trim();
            if (!destinationFileName.isEmpty()) {
                File destination = new File(destinationFileName);
                if (isCutOperation) {
                    if (clipboardFile.renameTo(destination)) {
                        updateFileList();
                        JOptionPane.showMessageDialog(this, "File moved successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to move file.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    try {
                        Files.copy(clipboardFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(this, "File copied successfully.");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Error copying file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a destination file name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Clipboard is empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renameFile() {
        File selectedFile = fileList.getSelectedValue();
        if (selectedFile != null) {
            String newFileName = JOptionPane.showInputDialog(this, "Enter new file name:");
            if (newFileName != null && !newFileName.trim().isEmpty()) {
                File newFile = new File(selectedFile.getParent(), newFileName);
                if (selectedFile.renameTo(newFile)) {
                    updateFileList();
                    JOptionPane.showMessageDialog(this, "File renamed successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to rename file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a new file name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file to rename.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFileList() {
        listModel.clear();
        File[] files = new File(".").listFiles();
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName)); // Sorting algorithm for files
            for (File file : files) {
                listModel.addElement(file);
            }
        }
    }

    private void updateFileListByName() {
        listModel.clear();
        File[] files = new File(".").listFiles();
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName)); // Sort by name
            for (File file : files) {
                listModel.addElement(file);
            }
        }
    }

    private void updateFileListByType() {
        listModel.clear();
        File[] files = new File(".").listFiles();
        if (files != null) {
            Arrays.sort(files, (f1, f2) -> {
                if (f1.isDirectory() && !f2.isDirectory()) {
                    return -1;
                } else if (!f1.isDirectory() && f2.isDirectory()) {
                    return 1;
                } else {
                    return getFileExtension(f1).compareTo(getFileExtension(f2));
                }
            });
            for (File file : files) {
                listModel.addElement(file);
            }
        }
    }

    private void updateFileListByDate() {
        listModel.clear();
        File[] files = new File(".").listFiles();
        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            for (File file : files) {
                listModel.addElement(file);
            }
        }
    }

    private void sortByFileName() {
        updateFileListByName();
    }

    private void sortByFileType() {
        updateFileListByType();
    }

    private void sortByFileDate() {
        updateFileListByDate();
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Operating_System::new);
    }
}

class FileListCellRenderer extends DefaultListCellRenderer {
    private static final Icon FOLDER_ICON = FileSystemView.getFileSystemView().getSystemIcon(new File("."));

    // Map to store icons for different file extensions
    private static final Map<String, Icon> FILE_TYPE_ICONS = new HashMap<>();
    private JList<File> fileList; // Add fileList variable

    // Constructor to initialize fileList
    public FileListCellRenderer(JList<File> fileList) {
        this.fileList = fileList;
    }

    static {
        // Load icons for common file types
        FILE_TYPE_ICONS.put("pdf", new ImageIcon("C:\\Users\\Zone\\Desktop\\GUI\\pdf.png")); // PDF file icon
        FILE_TYPE_ICONS.put("txt", new ImageIcon("C:\\Users\\Zone\\Desktop\\GUI\\txt.png")); // Text file icon
        FILE_TYPE_ICONS.put("doc", new ImageIcon("C:\\Users\\Zone\\Desktop\\GUI\\doc.png")); // Word document icon
        FILE_TYPE_ICONS.put("docx", new ImageIcon("C:\\Users\\Zone\\Desktop\\GUI\\doc.png")); // Word document icon
        FILE_TYPE_ICONS.put("xls", new ImageIcon("C:\\Users\\Zone\\Desktop\\GUI\\xls.png")); // Excel spreadsheet icon
        FILE_TYPE_ICONS.put("xlsx", new ImageIcon("C:\\Users\\Zone\\Desktop\\GUI\\xls.png")); // Excel spreadsheet icon
        FILE_TYPE_ICONS.put("jpg", new ImageIcon("‪‪C:\\Users\\Zone\\Desktop\\GUI\\icons8-jpeg-48.png")); // JPG image icon
        FILE_TYPE_ICONS.put("jpeg", new ImageIcon("C:\\Users\\Zone\\Desktop\\GUI\\icons8-jpeg-48.png")); // JPEG image icon
        FILE_TYPE_ICONS.put("png", new ImageIcon("‪‪C:\\Users\\Zone\\Desktop\\GUI\\icons8-jpeg-48.png")); // PNG image icon
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        File file = (File) value;
        setIcon(getResizedIcon(file));
        setText(file.getName());
        return this;
    }

    private Icon getResizedIcon(File file) {
        Icon icon;
        if (file.isDirectory()) {
            icon = FOLDER_ICON;
        } else {
            String extension = getFileExtension(file);
            icon = FILE_TYPE_ICONS.get(extension.toLowerCase());
            if (icon == null) {
                icon = UIManager.getIcon("FileView.fileIcon");
            }
        }
        if (icon instanceof ImageIcon) {
            ImageIcon imageIcon = (ImageIcon) icon;
            Image image = imageIcon.getImage();
            int size = fileList.getFont().getSize();
            Image newImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(newImage);
        }
        return icon;
    }
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
}