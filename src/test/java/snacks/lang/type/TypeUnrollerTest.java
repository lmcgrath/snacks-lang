package snacks.lang.type;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static snacks.lang.Types.*;

import org.junit.Before;
import org.junit.Test;
import snacks.lang.Type;
import snacks.lang.TypeUnroller;

public class TypeUnrollerTest {

    private Type parentType;
    private Type childType;

    @Before
    public void setUp() {
        parentType = algebraic("Tree", asList(simple("Integer")), asList(
            simple("Leaf"),
            record("Node", asList(
                property("_0", simple("Integer")),
                property("_1", recur("Tree", asList(simple("Integer")))),
                property("_2", recur("Tree", asList(simple("Integer"))))
            ))
        ));
        childType = record("Node", asList(
            property("_0", simple("Integer")),
            property("_1", recur("Tree", asList(simple("Integer")))),
            property("_2", recur("Tree", asList(simple("Integer"))))
        ));
    }

    @Test
    public void shouldUnrollChildType() {
        assertThat(new TypeUnroller(childType, parentType).unroll(), equalTo(record("Node", asList(
            property("_0", simple("Integer")),
            property("_1", algebraic("Tree", asList(simple("Integer")), asList(
                simple("Leaf"),
                recur("Node", asList(simple("Integer")))
            ))),
            property("_2", algebraic("Tree", asList(simple("Integer")), asList(
                simple("Leaf"),
                recur("Node", asList(simple("Integer")))
            )))
        ))));
    }
}
