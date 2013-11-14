package snacks.lang;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.doubleType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.integerType;
import static snacks.lang.Types.union;

import org.junit.Before;
import org.junit.Test;
import snacks.lang.runtime.SnacksClassLoader;

public class SnacksClassLoaderTest {

    private SnacksClassLoader loader;

    @Before
    public void setUp() {
        loader = new SnacksClassLoader();
    }

    @Test
    public void shouldResolveDivideSnack() {
        assertThat(loader.typeOf("snacks.lang./", EXPRESSION), equalTo(union(
            func(integerType(), func(integerType(), integerType())),
            func(integerType(), func(doubleType(), doubleType())),
            func(doubleType(), func(integerType(), doubleType())),
            func(doubleType(), func(doubleType(), doubleType()))
        )));
    }

    @Test
    public void shouldReturnNullIfSnackDoesNotExist() {
        assertThat(loader.typeOf("snacks.lang.waffles", EXPRESSION), nullValue());
    }
}
