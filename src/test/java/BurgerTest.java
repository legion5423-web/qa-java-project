import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class BurgerTest {

    private Burger burger;

    @Mock
    private Bun mockBun;

    @Mock
    private Ingredient mockIngredient1;

    @Mock
    private Ingredient mockIngredient2;

    private final boolean moveUp;
    private final int fromIndex;
    private final int toIndex;


    // Конструктор для параметризованных тестов
    public BurgerTest(boolean moveUp, int fromIndex, int toIndex) {
        this.moveUp = moveUp;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true, 1, 0},   // Перемещение вверх
                {false, 0, 1},  // Перемещение вниз
        });
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        burger = new Burger();

        // Настройка моков для булочки
        when(mockBun.getName()).thenReturn("White Bun");
        when(mockBun.getPrice()).thenReturn(2.5f);

        // Настройка моков для ингредиентов
        when(mockIngredient1.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredient1.getName()).thenReturn("Hot Sauce");
        when(mockIngredient1.getPrice()).thenReturn(1.0f);

        when(mockIngredient2.getType()).thenReturn(IngredientType.FILLING);
        when(mockIngredient2.getName()).thenReturn("Cheese");
        when(mockIngredient2.getPrice()).thenReturn(1.5f);
    }

    @Test
    public void testSetBuns() {
        burger.setBuns(mockBun);

        // Проверяем, что булочка установлена
        burger.getReceipt(); // Этот метод должен использовать установленную булочку
        verify(mockBun, atLeastOnce()).getName();
    }

    @Test
    public void testAddIngredient() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);

        assertEquals("Должен быть 1 ингредиент", 1, burger.ingredients.size());
    }

    @Test
    public void testRemoveIngredient() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
        burger.removeIngredient(0);

        assertEquals("Ингредиент должен быть удален", 0, burger.ingredients.size());
    }

    @Test
    public void testMoveIngredient() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        // Сохраняем исходный порядок
        Ingredient firstBeforeMove = burger.ingredients.get(0);
        Ingredient secondBeforeMove = burger.ingredients.get(1);

        // Перемещаем ингредиент
        if (moveUp) {
            burger.moveIngredient(fromIndex, toIndex);
            // После перемещения вверх, второй должен стать первым
            assertEquals("Ингредиенты должны поменяться местами",
                    secondBeforeMove, burger.ingredients.get(0));
        } else {
            burger.moveIngredient(fromIndex, toIndex);
            // После перемещения вниз, первый должен стать вторым
            assertEquals("Ингредиенты должны поменяться местами",
                    firstBeforeMove, burger.ingredients.get(1));
        }
    }

    @Test
    public void testGetPrice() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        // Ожидаемая цена: 2 булочки * 2.5 + 1.0 + 1.5 = 7.5
        float expectedPrice = 2 * 2.5f + 1.0f + 1.5f;
        float actualPrice = burger.getPrice();

        assertEquals("Цена рассчитана неверно", expectedPrice, actualPrice, 0.01);
    }

    @Test
    public void testGetReceipt() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);

        String receipt = burger.getReceipt();

        assertNotNull("Чек не должен быть null", receipt);
        assertTrue("Чек должен содержать название булочки",
                receipt.contains("White Bun"));
        assertTrue("Чек должен содержать название ингредиента",
                receipt.contains("Hot Sauce"));
        assertTrue("Чек должен содержать итоговую цену",
                receipt.contains("Price:"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIngredientInvalidIndex() {
        burger.setBuns(mockBun);
        burger.removeIngredient(999); // Несуществующий индекс
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientInvalidIndex() {
        burger.setBuns(mockBun);
        burger.moveIngredient(0, 999); // Несуществующий индекс
    }

    @Test
    public void testBurgerWithNoIngredients() {
        burger.setBuns(mockBun);

        float price = burger.getPrice();
        String receipt = burger.getReceipt();

        // Только цена булочек
        assertEquals(5.0f, price, 0.01);
        assertTrue(receipt.contains("White Bun"));
    }

    @RunWith(Parameterized.class)
    public static class BurgerPriceCalculationTest {

        private final int bunPrice;
        private final int ingredientPrice1;
        private final int ingredientPrice2;
        private final float expectedTotal;

        public BurgerPriceCalculationTest(int bunPrice, int ingredientPrice1,
                                          int ingredientPrice2, float expectedTotal) {
            this.bunPrice = bunPrice;
            this.ingredientPrice1 = ingredientPrice1;
            this.ingredientPrice2 = ingredientPrice2;
            this.expectedTotal = expectedTotal;
        }

        @Parameterized.Parameters(name = "Булочка: {0}, Ингредиент1: {1}, Ингредиент2: {2}, Итого: {3}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {100, 50, 75, 325},    // 2*100 + 50 + 75 = 325
                    {200, 0, 100, 500},    // 2*200 + 0 + 100 = 500
                    {50, 25, 25, 150},     // 2*50 + 25 + 25 = 150
                    {0, 0, 0, 0},          // Бесплатный бургер
            });
        }

        @Test
        public void testPriceCalculationWithDifferentPrices() {
            // Создаем моки
            Bun mockBun = mock(Bun.class);
            Ingredient mockIngredient1 = mock(Ingredient.class);
            Ingredient mockIngredient2 = mock(Ingredient.class);

            // Настраиваем моки
            when(mockBun.getPrice()).thenReturn((float) bunPrice);
            when(mockIngredient1.getPrice()).thenReturn((float) ingredientPrice1);
            when(mockIngredient2.getPrice()).thenReturn((float) ingredientPrice2);

            // Создаем и настраиваем бургер
            Burger burger = new Burger();
            burger.setBuns(mockBun);
            burger.addIngredient(mockIngredient1);
            burger.addIngredient(mockIngredient2);

            // Проверяем расчет цены
            float actualPrice = burger.getPrice();
            assertEquals("Неверный расчет цены", expectedTotal, actualPrice, 0.01);
        }
    }
}
