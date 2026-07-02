package com.bobsgame.editor.MapCanvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bobsgame.editor.Undo.UndoManager;
import com.bobsgame.editor.Undo.UndoableEdit;

public class MapHistoryPanel extends JPanel implements ChangeListener, ActionListener {

    private static final long serialVersionUID = 1L;
    private MapCanvas mapCanvas;

    private JList<UndoableEdit> historyList;
    private DefaultListModel<UndoableEdit> historyListModel;
    private JButton clearButton;

    public MapHistoryPanel(MapCanvas canvas) {
        this.mapCanvas = canvas;
        setLayout(new BorderLayout());

        historyListModel = new DefaultListModel<>();
        historyList = new JList<>(historyListModel);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setCellRenderer(new HistoryCellRenderer());

        mapCanvas.undoManager.addChangeListener(this);

        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = historyList.locationToIndex(e.getPoint());
                if(index != -1) {
                    UndoManager um = mapCanvas.undoManager;
                    int currentNext = um.getNextEditIndex();
                    int targetNext = index + 1;

                    if (targetNext < currentNext) {
                        while (um.getNextEditIndex() > targetNext) {
                            um.undo();
                        }
                    } else if (targetNext > currentNext) {
                        while (um.getNextEditIndex() < targetNext) {
                            um.redo();
                        }
                    }

                    mapCanvas.repaint();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyList);
        add(scrollPane, BorderLayout.CENTER);

        clearButton = new JButton("Clear History");
        clearButton.addActionListener(this);
        add(clearButton, BorderLayout.SOUTH);

        updateList();
    }

    public void updateList() {
        UndoManager um = mapCanvas.undoManager;
        if (um == null) return;

        historyListModel.clear();
        for(UndoableEdit edit : um.getEdits()) {
            historyListModel.addElement(edit);
        }

        int nextIndex = um.getNextEditIndex();
        if(nextIndex > 0) {
            historyList.setSelectedIndex(nextIndex - 1);
            historyList.ensureIndexIsVisible(nextIndex - 1);
        } else {
            historyList.clearSelection();
        }

        historyList.repaint();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        updateList();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == clearButton) {
            mapCanvas.undoManager.discardAllEdits();
            mapCanvas.repaint();
        }
    }

    class HistoryCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof UndoableEdit) {
                UndoableEdit edit = (UndoableEdit) value;
                setText(edit.getPresentationName());

                UndoManager um = mapCanvas.undoManager;
                int nextIndex = um.getNextEditIndex();

                if (index >= nextIndex) {
                    setForeground(Color.GRAY);
                } else {
                    setForeground(Color.BLACK);
                }

                if (index == nextIndex - 1) {
                    setBackground(new Color(200, 255, 200));
                } else {
                    if (isSelected) {
                        setBackground(list.getSelectionBackground());
                    } else {
                        setBackground(list.getBackground());
                    }
                }
            }
            return this;
        }
    }
}
