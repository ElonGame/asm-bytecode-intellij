/*
 *
 *  Copyright 2011 Cédric Champeau
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.objectweb.asm.idea.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;

import org.objectweb.asm.idea.constant.GroovyCodeStyle;
import org.objectweb.asm.idea.util.Settings;

import java.awt.Component;
import java.util.EnumMap;
import java.util.Objects;

import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

public class ASMPluginConfigurationPanel {
    private JPanel contentPane;
    private JCheckBox skipDebugCheckBox;
    private JCheckBox skipFramesCheckBox;
    private JCheckBox skipCodeCheckBox;
    private JCheckBox expandFramesCheckBox;
    private JComboBox groovyCodeStyleComboBox;
    private JTextField cfrParams;
    private JLabel cfrLabel;

    public ASMPluginConfigurationPanel() {
    }

    public JComponent getRootPane() {
        return contentPane;
    }

    public void setData(Settings data) {
        skipDebugCheckBox.setSelected(data.isSkipDebug());
        skipFramesCheckBox.setSelected(data.isSkipFrames());
        skipCodeCheckBox.setSelected(data.isSkipCode());
        expandFramesCheckBox.setSelected(data.isExpandFrames());
        groovyCodeStyleComboBox.setSelectedItem(GroovyCodeStyle.valueOf(data.getCodeStyle()));
        cfrParams.setText(data.getCfrParams());
    }

    public void getData(Settings data) {
        data.setSkipDebug(skipDebugCheckBox.isSelected());
        data.setSkipFrames(skipFramesCheckBox.isSelected());
        data.setSkipCode(skipCodeCheckBox.isSelected());
        data.setExpandFrames(expandFramesCheckBox.isSelected());
        if (groovyCodeStyleComboBox.getSelectedItem() != null) {
            data.setCodeStyle(groovyCodeStyleComboBox.getSelectedItem().toString());
        } else {
            data.setCodeStyle(GroovyCodeStyle.LEGACY.toString());
        }
        data.setCfrParams(cfrParams.getText());
    }

    public boolean isModified(Settings data) {
        if (skipDebugCheckBox.isSelected() != data.isSkipDebug()) return true;
        if (skipFramesCheckBox.isSelected() != data.isSkipFrames()) return true;
        if (skipCodeCheckBox.isSelected() != data.isSkipCode()) return true;
        if (expandFramesCheckBox.isSelected() != data.isExpandFrames()) return true;
        if (!Objects.equals(groovyCodeStyleComboBox.getSelectedItem(), data.getCodeStyle())) return true;
        if (!cfrParams.getText().equals(data.getCfrParams())) return true;
        return false;
    }

    private void createUIComponents() {
        ComboBoxModel model = new EnumComboBoxModel<>(GroovyCodeStyle.class);
        groovyCodeStyleComboBox = new ComboBox(model);
        groovyCodeStyleComboBox.setRenderer(new GroovyCodeStyleCellRenderer());
    }

    private static class GroovyCodeStyleCellRenderer implements ListCellRenderer {
        private EnumMap<GroovyCodeStyle, JLabel> labels;

        private GroovyCodeStyleCellRenderer() {
            labels = new EnumMap<>(GroovyCodeStyle.class);
            for (GroovyCodeStyle codeStyle : GroovyCodeStyle.values()) {
                labels.put(codeStyle, new JLabel(codeStyle.label));
            }
        }

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            return labels.get(value);
        }
    }
}
