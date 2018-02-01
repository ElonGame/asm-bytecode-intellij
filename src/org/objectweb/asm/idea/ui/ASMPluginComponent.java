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
/**
 * Created by IntelliJ IDEA.
 * User: cedric
 * Date: 18/01/11
 * Time: 19:51
 */

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.IconLoader;

import org.jetbrains.annotations.Nls;
import org.objectweb.asm.idea.util.Settings;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * A component created just to be able to configure the plugin.
 */
public class ASMPluginComponent implements Configurable,Disposable {

    private Settings settings;

    private ASMPluginConfigurationPanel configDialog;

    public ASMPluginComponent() {
        settings = Settings.getInstance();
    }

    // -------------- Configurable interface implementation --------------------------

    @Nls
    @Override
    public String getDisplayName() {
        return "ASM Bytecode plugin";
    }

    public Icon getIcon() {
        return IconLoader.getIcon("/images/asm.gif");
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        if (configDialog==null) configDialog = new ASMPluginConfigurationPanel();
        return configDialog.getRootPane();
    }

    @Override
    public boolean isModified() {
        return configDialog!=null && configDialog.isModified(settings);
    }

    @Override
    public void apply() {
        if (configDialog!=null) {
            configDialog.getData(settings);
        }
    }

    @Override
    public void reset() {
        if (configDialog!=null) {
            configDialog.setData(settings);
        }
    }

    @Override
    public void disposeUIResources() {
        configDialog = null;
    }

    @Override
    public void dispose() {
        this.configDialog = null;
    }
}


