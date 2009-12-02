package de.elateportal.editor.components.listeditor;

import java.util.Collections;

import org.apache.wicket.ResourceReference;

public class MoveDownButton extends EditorButton {

    public MoveDownButton(final String id) {
        super(id, new ResourceReference(MoveDownButton.class, "images/down.png"));
    }

    @Override
    public boolean isEnabled() {
        return getEditor().canMoveDown(getItem());
    }

    @Override
    public void onSubmit() {
        final int idx = getItem().getIndex();
        final int itemCount = getItem().getParent().size();

        if (idx < itemCount - 1 && itemCount > 1) {
            Collections.swap(getList(), idx, idx + 1);
            resetInputFields(idx,idx+1);
        }
    }
}
