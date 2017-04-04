package cz.zcu.fav.remotestimulatorcontrol.ui.configurations;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils;
import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.io.ExtensionType;
import cz.zcu.fav.remotestimulatorcontrol.model.configuration.ConfigurationType;
import cz.zcu.fav.remotestimulatorcontrol.ui.MainActivity;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.childAtPosition;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.clearConfigurations;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.clearSharedPrefs;
import static cz.zcu.fav.remotestimulatorcontrol.EspressoTestUtils.openDrawer;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;

/**
 * Parametrizovaný test pro otestování vytvoření všech druhů konfigurací
 * ve všech dostupných formátech
 */
@LargeTest
@RunWith(Parameterized.class)
public class ConfigurationsFragmentParametrizedTest {

    @Parameterized.Parameters()
    public static Collection<Object[]> values() {
        return Arrays.asList(new Object[][]{
                {ConfigurationType.ERP, R.string.experiment_erp, ExtensionType.XML},
                {ConfigurationType.ERP, R.string.experiment_erp, ExtensionType.JSON},
                {ConfigurationType.ERP, R.string.experiment_erp, ExtensionType.CSV},
                {ConfigurationType.FVEP, R.string.experiment_fvep, ExtensionType.XML},
                {ConfigurationType.FVEP, R.string.experiment_fvep, ExtensionType.JSON},
                {ConfigurationType.FVEP, R.string.experiment_fvep, ExtensionType.CSV},
                {ConfigurationType.TVEP, R.string.experiment_tvep, ExtensionType.XML},
                {ConfigurationType.TVEP, R.string.experiment_tvep, ExtensionType.JSON},
                {ConfigurationType.TVEP, R.string.experiment_tvep, ExtensionType.CSV},
                {ConfigurationType.CVEP, R.string.experiment_cvep, ExtensionType.XML},
                {ConfigurationType.CVEP, R.string.experiment_cvep, ExtensionType.JSON},
                {ConfigurationType.CVEP, R.string.experiment_cvep, ExtensionType.CSV},
                {ConfigurationType.REA, R.string.experiment_rea, ExtensionType.XML},
                {ConfigurationType.REA, R.string.experiment_rea, ExtensionType.JSON},
                {ConfigurationType.REA, R.string.experiment_rea, ExtensionType.CSV},
        });
    }

    @Parameterized.Parameter()
    public ConfigurationType type;
    @Parameterized.Parameter(1)
    public int configurationTypeNameResource;
    @Parameterized.Parameter(2)
    public ExtensionType extension;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private String configurationName;

    @Before
    public void setUp() throws Exception {
        clearConfigurations(getTargetContext());
        clearSharedPrefs(getTargetContext());

        configurationName = type.name() + "_test_" + extension.name();
    }

    @After
    public void tearDown() throws Exception {
        clearConfigurations(getTargetContext());
        clearSharedPrefs(getTargetContext());
    }

    @Test
    public void parametrizedTest() throws Exception {
        // Test, že je zobrazena hláška, že není nalezena žádná konfigurace
        onView(
                withId(R.id.textNoConfigurationFound))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.no_configuration_found)));

        // Otevřít postraní navigaci
        openDrawer();

        // Kliknout na nastavení
        onView(
                allOf(
                        withId(R.id.design_menu_item_text),
                        withText(R.string.nav_text_settings),
                        isDisplayed()))
                .perform(click());

        // Vybrat záloku "Obecné"
        onView(
                allOf(
                        childAtPosition(
                                allOf(
                                        withId(android.R.id.list),
                                        withParent(withClassName(is("android.widget.LinearLayout")))),
                                0),
                        isDisplayed()))
                .perform(click());

        // Zobrazit výběr typu souboru
        onView(
                allOf(
                        childAtPosition(
                                withId(android.R.id.list),
                                0),
                        isDisplayed()))
                .perform(click());

        // Vybrat poadovaný typ souboru
        onView(
                withText(extension.name()))
                .perform(click());

        // Zvolit možnost zobrazit typ souboru ve výpisu
        onView(
                allOf(
                        childAtPosition(
                                withId(android.R.id.list),
                                1),
                        isDisplayed()))
                .perform(click());

        // Odejít z nastavení
        pressBack();
        pressBack();

        EspressoTestUtils.createConfiguration(getTargetContext(),
                configurationName, configurationTypeNameResource);

        // Zkontroluji, že je zobrazen obrázek se správným textem
        onView(
                allOf(
                        withId(R.id.text_configuration_type),
                        isDisplayed()))
                .check(matches(withText(equalToIgnoringCase(type.name()))));

        // Zkontroluji, že je zobrazen správný název konfigurace
        onView(
                allOf(
                        withId(R.id.text_configuration_name),
                        isDisplayed()))
                .check(matches(withText(equalToIgnoringCase(configurationName))));

        // Zkontroluji, že konfigurace je uložena v požadovaném souboru
        onView(
                allOf(
                        withId(R.id.text_extension),
                        isDisplayed()))
                .check(matches(withText(equalToIgnoringCase(extension.toString()))));
    }
}
