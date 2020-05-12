package com.haulmont.sample.petclinic.web.screens.main;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.Notifications.NotificationType;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.Timer.TimerActionEvent;
import com.haulmont.cuba.gui.components.mainwindow.SideMenu;
import com.haulmont.cuba.gui.components.mainwindow.SideMenu.MenuItem;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.WebConfig;
import com.haulmont.cuba.web.app.main.MainScreen;
import com.haulmont.cuba.web.gui.MainTabSheetMode;
import com.haulmont.cuba.web.gui.components.mainwindow.WebAppWorkArea;
import com.haulmont.cuba.web.widgets.CubaManagedTabSheet;
import com.haulmont.cuba.web.widgets.CubaTabSheet;
import com.haulmont.cuba.web.widgets.HasTabSheetBehaviour;
import com.haulmont.sample.petclinic.entity.visit.Visit;
import com.haulmont.sample.petclinic.web.screens.visit.MyVisits;

import javax.inject.Inject;


@UiController("extMainScreen")
@UiDescriptor("ext-main-screen.xml")
public class ExtMainScreen extends MainScreen {
    @Inject
    protected SideMenu sideMenu;

    @Inject
    protected DataManager dataManager;
    @Inject
    protected UserSession userSession;
    @Inject
    protected ScreenBuilders screenBuilders;

    @Subscribe
    protected void initMainMenu(AfterShowEvent event) {
        createMyVisitMenuItem();
        openPetclinicMenuItem();
    }


    private void openPetclinicMenuItem() {
        final MenuItem petclinicMenu = sideMenu.getMenuItem("application-petclinic");
        final MenuItem menuItem = petclinicMenu.getChildren().get(1);
        petclinicMenu.setExpanded(true);
        sideMenu.setSelectOnClick(true);
        sideMenu.setSelectedItem(menuItem);
    }

    private void createMyVisitMenuItem() {
        MenuItem myVisits = sideMenu.createMenuItem("myVisits");
        myVisits.setBadgeText(amountOfVisits() + " Visits");
        myVisits.setCaption("My Visits");
        myVisits.setCommand(menuItem ->
            screenBuilders.screen(this)
            .withScreenClass(MyVisits.class)
            .withOpenMode(OpenMode.DIALOG)
                .show()
                );
        sideMenu.addMenuItem(myVisits, 0);
    }

    private int amountOfVisits() {
        return dataManager.load(Visit.class)
                .query("e.assignedNurse = :currentUser")
                .parameter("currentUser", userSession.getCurrentOrSubstitutedUser())
                .list().size();
    }

    @Subscribe("refreshMyVisits")
    protected void onRefreshMyVisitsTimerAction(TimerActionEvent event) {
        sideMenu.getMenuItem("myVisits")
                .setBadgeText(amountOfVisits() + " Visits");
    }


}