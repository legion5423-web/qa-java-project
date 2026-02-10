import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class BurgerInvalidIndexTest {

    private Burger burger;

    @Mock
    private Bun mockBun;

    @Mock
    private Ingredient mockIngredient1;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        burger = new Burger();

        when(mockBun.getName()).thenReturn("Test Bun");
        when(mockIngredient1.getName()).thenReturn("Ingredient 1");

        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientInvalidFromIndex() {
        burger.moveIngredient(999, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientInvalidToIndex() {
        burger.moveIngredient(0, 999);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientBothInvalidIndexes() {
        burger.moveIngredient(999, 1000);
    }
}