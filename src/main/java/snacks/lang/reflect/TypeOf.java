package snacks.lang.reflect;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.*;

import snacks.lang.*;
import snacks.lang.ListType.EmptyList;
import snacks.lang.Maybe.Nothing;
import snacks.lang.reflect.TypeInfo.FunctionInfo;
import snacks.lang.reflect.TypeInfo.RecordInfo;
import snacks.lang.reflect.TypeInfo.SimpleInfo;
import snacks.lang.type.Type;

@Snack(name = "typeOf", kind = EXPRESSION)
public class TypeOf {

    private static TypeOf instance;

    public static TypeOf instance() {
        if (instance == null) {
            instance = new TypeOf();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("snacks.lang.typeOf#a"), TypeInfo.type());
    }

    public Object apply(Integer expression) {
        return new SimpleInfo(Symbol.valueOf(INTEGER_TYPE.getName()), Nothing.value());
    }

    public Object apply(String expression) {
        return new SimpleInfo(Symbol.valueOf(STRING_TYPE.getName()), Nothing.value());
    }

    public Object apply(Double expression) {
        return new SimpleInfo(Symbol.valueOf(DOUBLE_TYPE.getName()), Nothing.value());
    }

    public Object apply(Boolean expression) {
        return new SimpleInfo(Symbol.valueOf(BOOLEAN_TYPE.getName()), Nothing.value());
    }

    public Object apply(Character expression) {
        return new SimpleInfo(Symbol.valueOf(CHARACTER_TYPE.getName()), Nothing.value());
    }

    public Object apply(Symbol expression) {
        return new SimpleInfo(Symbol.valueOf(SYMBOL_TYPE.getName()), Nothing.value());
    }

    public Object apply(_Constant expression) {
        return new SimpleInfo(nameOf(expression), Nothing.value());
    }

    public Object apply(_Function expression) {
        return new FunctionInfo(null, null);
    }

    public Object apply(_Record record) {
        return new RecordInfo(nameOf(record), EmptyList.value(), EmptyList.value(), Nothing.value());
    }

    public Object apply(_Tuple expression) {
        int size = expression.getClass().getDeclaredFields().length;
        return new RecordInfo(Symbol.valueOf("snacks.lang.Tuple" + size), EmptyList.value(), EmptyList.value(), Nothing.value());
    }

    public Object apply(Object expression) {
        throw new SnacksException("Class " + expression.getClass().getName() + " has no type!");
    }

    private Symbol nameOf(Object expression) {
        String name = expression.getClass().getAnnotation(Snack.class).name();
        String module = expression.getClass().getName().substring(0, expression.getClass().getName().lastIndexOf('.'));
        return Symbol.valueOf(module + '.' + name);
    }
}
