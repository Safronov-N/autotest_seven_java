package ru.safronov.autotest.seven.java.elementModels;

import org.openqa.selenium.WebElement;
import ru.safronov.autotest.seven.java.elementModels.AbstractElement;
import ru.safronov.autotest.seven.java.interfaces.IElementCollection;
import ru.safronov.autotest.seven.java.utils.CustomLogger;
import ru.safronov.autotest.seven.java.utils.ElementFuture;
import ru.safronov.autotest.seven.java.utils.ElementFutureResolved;
import ru.safronov.autotest.seven.java.utils.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MenuGroup extends AbstractElement implements IElementCollection<MenuItem> {

    private Collection<MenuItem> menuItems;

    public MenuGroup(ElementFuture future) {
        super(future);
            List<WebElement> collection = future.resolveMany();
            menuItems = new ArrayList<MenuItem>(collection.size());
            for (WebElement element : collection) {
                menuItems.add(new MenuItem(new ElementFutureResolved(element)));
        }
    }


    @Override
    public boolean exists(String itemDescription) {
        return false;
    }

    @Override
    public MenuItem get(String itemDescription) {
        for (int i = 0; i < 3; i++) {
            for (MenuItem item : menuItems)
                if (item.future.resolve().getText().equals(itemDescription)) {
                    return item;
                }
            menuItems.clear();
            for (WebElement element : future.resolveMany()) {
                menuItems.add(new MenuItem(new ElementFutureResolved(element)));
            }
        }
        return null;
    }

    @Override
    public void select(String itemDescription) {
        CustomLogger.info(String.format("Клик по элементу '%s'", itemDescription));
        get(itemDescription).future.resolve().click();
    }

    @Override
    public Iterator<MenuItem> iterator() {
        return menuItems.iterator();
    }

    @Override
    public WebElement getInitialElement() {
        throw new RuntimeException(new IllegalAccessException());
    }
}
