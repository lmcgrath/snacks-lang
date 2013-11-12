package snacks.lang.runtime;

import org.junit.Test;

public class ReflectTest extends AbstractRuntimeTest {

    @Test
    public void shouldGetNamesOfTypes() {
        run(
            "data Toast = { bread: Symbol, burned?: Boolean }",
            "data Point = 0D | 1D Integer | 2D Integer Integer | 3D Integer Integer Integer",

            "toast = Toast { bread = :rye, burned? = True }",
            "point2d = 2D 5 (-3)",
            "tuple = (1, 'toast')",

            "main = () -> {",
            "    assert $ (typeOf (!)     $> name) == :'snacks.lang.FunctionType'",
            "    assert $ (typeOf 12.3    $> name) == :'snacks.lang.Double'",
            "    assert $ (typeOf tuple   $> name) == :'snacks.lang.Tuple2'",
            "    assert $ (typeOf toast   $> name) == :'test.Toast'",
            "    assert $ (typeOf point2d $> name) == :'test.2D'",
            "    assert $ (typeOf 0D      $> name) == :'test.0D'",
            "}"
        );
    }

    @Test
    public void shouldGetTypeByName() {
        run(
            "data Toast = { bread: Symbol, burned?: Boolean }",
            "main = () -> {",
            "    assert $ typeFor (TypeName :'test.Toast') == typeOf Toast { bread = :sourdough, burned? = True }",
            "    assert $ typeFor (ExpressionName :'test.Toast') == typeOf Toast",
            "}"
        );
    }
}
