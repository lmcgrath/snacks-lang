package snacks.lang;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.DOUBLE_TYPE;
import static snacks.lang.type.Types.INTEGER_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.union;

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
            func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)),
            func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)),
            func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)),
            func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE))
        )));
    }

    @Test
    public void shouldReturnNullIfSnackDoesNotExist() {
        assertThat(loader.typeOf("snacks.lang.waffles", EXPRESSION), nullValue());
    }
}
