package org.bukkit.plugin.localization;

import org.bukkit.TestCommandSender;
import org.bukkit.entity.TestPlayer;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocaleManagerTest {
    @Test(expected = IllegalArgumentException.class)
    public void testSetDefaultWithoutLoading() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().setDefaultLocale(Locale.US);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTryToRegisterEmptyLoader() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().registerLoader(null);
    }

    @Test
    public void testRegisterInMemoryResourceLoader() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().registerLoader(new InMemoryResourceLoader());
        testPlugin.getLocaleManager().load(Locale.US, "This is a in Memory Test:inmemory");
        testPlugin.getLocaleManager().setDefaultLocale(Locale.US);

        assertThat(testPlugin.getLocaleManager().translate(new TestCommandSender(), "test"), is("This is a in Memory Test"));
    }

    @Test(expected = ResourceNotLoadedException.class)
    public void testTranslateKeyWhichIsNotLoaded() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().registerLoader(new InMemoryResourceLoader());
        testPlugin.getLocaleManager().load(Locale.US, "This is a in Memory Test:inmemory");
        testPlugin.getLocaleManager().setDefaultLocale(Locale.US);
        testPlugin.getLocaleManager().translate(new TestCommandSender(), "test1");
    }

    @Test
    public void testDefaultLocaleForPlayer() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().registerLoader(new InMemoryResourceLoader());
        testPlugin.getLocaleManager().load(Locale.US, "This is a in Memory Test:inmemory");
        testPlugin.getLocaleManager().setDefaultLocale(Locale.US);

        assertThat(testPlugin.getLocaleManager().translate(new TestPlayer(), "test"), is("This is a in Memory Test"));
    }

    @Test
    public void testPlayerLocale() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().registerLoader(new InMemoryResourceLoader());
        testPlugin.getLocaleManager().load(Locale.US, "This is a in Memory Test:inmemory");
        testPlugin.getLocaleManager().load(new Locale("de", "DE"), "Dies ist ein RAM Test:inmemory");
        testPlugin.getLocaleManager().setDefaultLocale(Locale.US);

        assertThat(testPlugin.getLocaleManager().translate(new TestPlayer(), "test"), is("Dies ist ein RAM Test"));
    }

    @Test(expected = ResourceNotLoadedException.class)
    public void testPlayerLocaleNotLoadedKey() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().registerLoader(new InMemoryResourceLoader());
        testPlugin.getLocaleManager().load(Locale.US, "This is a in Memory Test:inmemory");
        testPlugin.getLocaleManager().load(new Locale("de", "DE"), "Dies ist ein RAM Test:inmemory");
        testPlugin.getLocaleManager().setDefaultLocale(Locale.US);
        testPlugin.getLocaleManager().translate(new TestPlayer(), "test1");
    }

    @Test(expected = NullPointerException.class)
    public void testCleanup() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().registerLoader(new InMemoryResourceLoader());
        testPlugin.getLocaleManager().load(Locale.US, "This is a in Memory Test:inmemory");
        testPlugin.getLocaleManager().load(new Locale("de", "DE"), "Dies ist ein RAM Test:inmemory");
        testPlugin.getLocaleManager().setDefaultLocale(Locale.US);
        testPlugin.getLocaleManager().cleanup();

        //A reload should throw a NullPointer since the ResourceManager has been cleared out
        testPlugin.getLocaleManager().reload();
    }

    @Test()
    public void testReload() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().registerLoader(new InMemoryResourceLoader());
        testPlugin.getLocaleManager().load(Locale.US, "This is a in Memory Test:inmemory");
        testPlugin.getLocaleManager().load(new Locale("de", "DE"), "Dies ist ein RAM Test:inmemory");
        testPlugin.getLocaleManager().setDefaultLocale(Locale.US);
        testPlugin.getLocaleManager().reload();
    }

    @Test()
    public void testLocaleChange() throws Exception {
        LocaleTestPlugin testPlugin = new LocaleTestPlugin("LocaleTest");
        testPlugin.getLocaleManager().registerLoader(new InMemoryResourceLoader());
        testPlugin.getLocaleManager().load(Locale.US, "This is a in Memory Test:inmemory");
        testPlugin.getLocaleManager().load(new Locale("de", "DE"), "Dies ist ein RAM Test:inmemory");
        testPlugin.getLocaleManager().setDefaultLocale(Locale.US);

        TestPlayer testPlayer = new TestPlayer();
        assertThat(testPlugin.getLocaleManager().translate(testPlayer, "test"), is("Dies ist ein RAM Test"));

        testPlayer.setLocale("en_US");
        assertThat(testPlugin.getLocaleManager().translate(testPlayer, "test"), is("This is a in Memory Test"));

        testPlayer.setLocale("cy_CZ");
        assertThat(testPlugin.getLocaleManager().translate(testPlayer, "test"), is("This is a in Memory Test"));
    }
}
