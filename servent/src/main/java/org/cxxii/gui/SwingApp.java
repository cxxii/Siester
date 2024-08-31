package org.cxxii.gui;

import org.cxxii.messages.PongMessage;
import org.cxxii.messages.QueryHitMessageParser;
import org.cxxii.messages.QueryMessage;
import org.cxxii.server.Server;
import org.cxxii.share.FileDownloader;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class SwingApp implements QueryHitListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(QueryHitMessageParser.class);
    private static DefaultListModel<String> listModel;
    private static JLabel activePeersLabel;
    private static Set<String> activePeers;

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("siester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel label = new JLabel("Search:");
        searchPanel.add(label);

        // Load SVG icon and convert to PNG for JLabel
        ImageIcon icon = new ImageIcon("src/main/resources/icon.png");
        JLabel iconLabel = new JLabel(icon);
        searchPanel.add(iconLabel);

        JTextField textField = new JTextField(20);
        searchPanel.add(textField);

        RoundedButton button = new RoundedButton("Search");
        searchPanel.add(button);

        RoundedButton downloadButton = new RoundedButton("Download");
        searchPanel.add(downloadButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        JList<String> fileList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        panel.add(scrollPane, BorderLayout.CENTER);


        JPanel statusPanel = new JPanel(new BorderLayout());
        activePeersLabel = new JLabel("Active Peers: 0");
        statusPanel.add(activePeersLabel, BorderLayout.WEST);
        panel.add(statusPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setSize(750, 500);
        frame.setVisible(true);

        // Add action listener to the button
        button.addActionListener(e -> {
            String searchQuery = textField.getText();
            if (!searchQuery.isEmpty()) {
                QueryMessage.makeQuery(searchQuery);
            }
        });

        downloadButton.addActionListener(e -> {
            String selectedFile = fileList.getSelectedValue();
            if (selectedFile != null) {
                try {
                    downloadFile(selectedFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No file selected", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void downloadFile(String fileName) throws IOException {
        LOGGER.info("Initiating download for: " + fileName);

        String outputFilePath = FileManager.getDownloadDirPath().toString() + "/" + fileName;
        FileDownloader.downloadFile(fileName, outputFilePath);

        JOptionPane.showMessageDialog(null, "Download started for: " + fileName, "Download", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onQueryHitReceived(List<String> fileNames) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (String fileName : fileNames) {
                listModel.addElement(fileName);
            }
        });
    }

    public static void updateActivePeersCount() {
        SwingUtilities.invokeLater(() -> activePeersLabel.setText("Active Peers: " + PongMessage.getHostListSize()));
    }
}
