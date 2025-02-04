/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jkiss.dbeaver.ui.controls.resultset.view;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.DBValueFormatting;
import org.jkiss.dbeaver.model.data.DBDAttributeBinding;
import org.jkiss.dbeaver.model.data.DBDDisplayFormat;
import org.jkiss.dbeaver.ui.UIStyles;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.UIWidgets;
import org.jkiss.dbeaver.ui.controls.resultset.AbstractPresentation;
import org.jkiss.dbeaver.ui.controls.resultset.IResultSetController;
import org.jkiss.dbeaver.ui.controls.resultset.ResultSetCopySettings;
import org.jkiss.dbeaver.ui.controls.resultset.ResultSetRow;
import org.jkiss.utils.CommonUtils;

import java.util.Collections;
import java.util.Map;

/**
 * Execution statistics presentation.
 * Special internal presentation for execution statistics visualization.
 */
public class StatisticsPresentation extends AbstractPresentation {

    private Table table;
    private DBDAttributeBinding curAttribute;

    @Override
    public void createPresentation(@NotNull IResultSetController controller, @NotNull Composite parent) {
        super.createPresentation(controller, parent);
        table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION);
        table.setLinesVisible(!UIStyles.isDarkTheme());
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                curAttribute = null;
                TableItem[] selection = table.getSelection();
                Object[] elements = new Object[selection.length];
                for (int i = 0; i < selection.length; i++) {
                    elements[i] = selection[i].getData();
                    if (curAttribute == null) {
                        curAttribute = (DBDAttributeBinding) elements[i];
                    }
                }
                fireSelectionChanged(new StructuredSelection(elements));
            }
        });

        UIUtils.createTableColumn(table, SWT.LEFT, "Name");
        UIUtils.createTableColumn(table, SWT.LEFT, "Value");

        UIWidgets.setControlContextMenu(table, manager -> UIWidgets.fillDefaultTableContextMenu(manager, table));
    }

    @Override
    public Control getControl() {
        return table;
    }

    @Override
    public void refreshData(boolean refreshMetadata, boolean append, boolean keepState) {
        table.removeAll();

        if (controller.getModel().getRowCount() > 0) {
            ResultSetRow row = controller.getModel().getRow(0);
            java.util.List<DBDAttributeBinding> visibleAttributes = controller.getModel().getVisibleAttributes();
            for (int i = 0; i < visibleAttributes.size(); i++) {
                DBDAttributeBinding attr = visibleAttributes.get(i);
                Object value = row.getValues()[i];
                String valueString = DBValueFormatting.getDefaultValueDisplayString(value, DBDDisplayFormat.UI);
                String[] lines = valueString.split("\n");
                for (int k = 0; k < lines.length; k++) {
                    String line = lines[k];
                    if (CommonUtils.isEmptyTrimmed(line)) {
                        continue;
                    }
                    line = line.replace("\t", "    ");
                    TableItem item = new TableItem(table, SWT.LEFT);
                    item.setText(0, k == 0 ? attr.getName() : "");
                    item.setText(1, line);
                    item.setData(attr);
                }
            }
        }

        UIUtils.packColumns(table);
    }

    @Override
    public void formatData(boolean refreshData) {

    }

    @Override
    public void clearMetaData() {

    }

    @Override
    public void updateValueView() {

    }

    @Override
    public void changeMode(boolean recordMode) {

    }

    @Nullable
    @Override
    public DBDAttributeBinding getCurrentAttribute() {
        return curAttribute;
    }

    @NotNull
    @Override
    public Map<Transfer, Object> copySelection(ResultSetCopySettings settings) {
        return Collections.emptyMap();
    }
}
