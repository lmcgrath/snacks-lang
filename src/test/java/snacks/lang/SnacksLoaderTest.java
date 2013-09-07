package snacks.lang;

import static org.fest.assertions.Assertions.assertThat;
import static snacks.lang.Type.DOUBLE_TYPE;
import static snacks.lang.Type.INTEGER_TYPE;
import static snacks.lang.Type.func;
import static snacks.lang.Type.set;

import org.junit.Before;
import org.junit.Test;

public class SnacksLoaderTest {

    private SnacksLoader loader;

    @Before
    public void setUp() {
        loader = new SnacksLoader();
    }

    @Test
    public void shouldResolveDivideSnack() {
        assertThat(loader.typeOf("snacks.lang./")).isEqualTo(set(
            func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)),
            func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)),
            func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)),
            func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE))
        ));
    }

    @Test
    public void shouldReturnNullIfSnackDoesNotExist() {
        assertThat(loader.typeOf("snacks.lang.waffles")).isNull();
    }
}
