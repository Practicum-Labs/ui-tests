import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.content.Intent
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage

@RunWith(AndroidJUnit4::class)
class UiTestsSprint8 {

    private val arguments = InstrumentationRegistry.getArguments()

    private val activityClassName: String = arguments.getString("activityClass")
        ?: throw IllegalArgumentException("No activityClass argument provided.")

    @Suppress("UNCHECKED_CAST")
    private val activityClass: Class<out Activity> = Class.forName(activityClassName) as Class<out Activity>

    @get:Rule
    val activityRule = ActivityScenarioRule(activityClass)

    // Set up and tear down methods for Intents
    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @SuppressLint("CheckResult")
    @Test
    fun `Проверка_основного_цвета_фона`() {
        val expectedColor = Color.parseColor("#3772E7") // Or your specific color

        onView(withId(android.R.id.content))
            .check(matches(hasChildWithBackgroundColor(expectedColor)))
    }

    // Форматирование текста на кнопках
    // - первая буква заглавная
    // - остальные буквы строчные
    @Test
    fun `Проверка_форматирования_текста_на_кнопке_ПОИСК`() {
        val buttonName = "Поиск"
        onView(withTextCaseInsensitive(buttonName))
            .check(
                matches(isWellFormattedText(buttonName))
            )
    }

    @Test
    fun `Проверка_форматирования_текста_на_кнопке_МЕДИАТЕКА`() {
        val buttonName = "Медиатека"
        onView(withTextCaseInsensitive(buttonName))
            .check(
                matches(isWellFormattedText(buttonName))
            )
    }

    @Test
    fun `Проверка_форматирования_текста_на_кнопке_НАСТРОЙКИ`() {
        val buttonName = "Настройки"
        onView(withTextCaseInsensitive(buttonName))
            .check(
                matches(isWellFormattedText(buttonName))
            )
    }

    // Проверка отступов у кнопок
    // - слева и справа у каждой кнопки должно быть по 16 dp
    // - между кнопками должно быть 16 dp
    // - сверху от кнопки "Поиск" до верхнего края экрана должно быть 24 dp
    // - снизу от кнопки "Настройки" до нижнего края экрана должно быть 24 dp

    @Test
    fun `Проверка_левого_отступа_кнопки_ПОИСК`() {
        val targetButtonText = "Поиск"
        checkSideMargins(targetButtonText, 16, onlyLeft = true)
    }

    @Test
    fun `Проверка_правого_отступа_кнопки_ПОИСК`() {
        val targetButtonText = "Поиск"
        checkSideMargins(targetButtonText, 16, onlyRight = true)
    }

    @Test
    fun `Проверка_верхнего_отступа_кнопки_ПОИСК`() {
        val buttonText = "Поиск"
        val marginDp = 24

        // Rects to store the bounds
        val buttonBounds = Rect()
        val elementAboveBounds = Rect()

        // Get bounds of the button
        onView(withTextCaseInsensitive(buttonText)).perform(GetViewBoundsAction(buttonBounds))

        // Find the element above the button and get its bounds
        onView(isAbove(withTextCaseInsensitive(buttonText))).perform(GetViewBoundsAction(elementAboveBounds))

        // Get screen density to convert DP to pixels
        val displayMetrics = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
        val screenDensity = displayMetrics.density

        // Calculate the minimum required space in pixels
        val minSpacePx = (marginDp * screenDensity).toInt()

        // Calculate the actual space
        val actualSpacePx = buttonBounds.top - elementAboveBounds.bottom

        // Assert the space is sufficient
        assertTrue(
            "Пространство между кнопкой \"$buttonText\" и элементом выше некорректно. " +
                    "Ожидается: $marginDp dp. " +
                    "Фактическое расстояние: ${actualSpacePx / screenDensity} dp.",
            actualSpacePx == minSpacePx
        )
    }

    @Test
    fun `Проверка_размера_иконки_для_кнопки_ПОИСК`() {
        val buttonText = "Поиск"
        val expectedIconSizeDp = 24

        onView(withTextCaseInsensitive(buttonText))
            .check(matches(hasDrawableSize(expectedIconSizeDp)))
    }

    @Test
    fun `Проверка_отступов_между_кнопками_ПОИСК_и_МЕДИАТЕКА`() {
        val targetButtonText = "Поиск"
        val secondButtonText = "Медиатека"
        checkVerticalSpaceBetweenViews(targetButtonText, secondButtonText, 16)
    }

    @Test
    fun `Проверка_левого_отступа_кнопки_МЕДИАТЕКА`() {
        val targetButtonText = "Медиатека"
        checkSideMargins(targetButtonText, 16, onlyLeft = true)
    }

    @Test
    fun `Проверка_правого_отступа_кнопки_МЕДИАТЕКА`() {
        val targetButtonText = "Медиатека"
        checkSideMargins(targetButtonText, 16, onlyRight = true)
    }

    @Test
    fun `Проверка_размера_иконки_для_кнопки_МЕДИАТЕКА`() {
        val buttonText = "Медиатека"
        val expectedIconSizeDp = 24

        onView(withTextCaseInsensitive(buttonText))
            .check(matches(hasDrawableSize(expectedIconSizeDp)))
    }

    @Test
    fun `Проверка_отступов_между_кнопками_МЕДИАТЕКА_и_НАСТРОЙКИ`() {
        val targetButtonText = "Медиатека"
        val secondButtonText = "Настройки"
        checkVerticalSpaceBetweenViews(targetButtonText, secondButtonText, 16)
    }

    @Test
    fun `Проверка_левого_отступа_кнопки_НАСТРОЙКИ`() {
        val targetButtonText = "Настройки"
        checkSideMargins(targetButtonText, 16, onlyLeft = true)
    }

    @Test
    fun `Проверка_правого_отступа_кнопки_НАСТРОЙКИ`() {
        val targetButtonText = "Настройки"
        checkSideMargins(targetButtonText, 16, onlyRight = true)
    }

    @Test
    fun `Проверка_нижнего_отступа_кнопки_НАСТРОЙКИ`() {
        val buttonText = "Настройки"
        val marginDp = 28 // Example minimum required margin

        // Rect to store the button's bounds
        val buttonBounds = Rect()

        // Get the button's coordinates on the screen
        onView(withTextCaseInsensitive(buttonText)).perform(GetViewBoundsAction(buttonBounds))

        // Get the screen dimensions to calculate the bottom margin
        val displayMetrics = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
        val screenHeightPx = displayMetrics.heightPixels
        val screenDensity = displayMetrics.density

        // Calculate the minimum margin in pixels
        val marginPx = (marginDp * screenDensity).toInt()

        // Calculate the actual space from the bottom of the button to the bottom of the screen
        val bottomMarginPx = screenHeightPx - buttonBounds.bottom

        // Assert that the bottom margin is greater than or equal to the minimum
        assertTrue(
            "Нижний отступ кнопки \"$buttonText\" некорректный. " +
                    "Ожидается: $marginDp dp. " +
                    "Фактическое расстояние: ${bottomMarginPx / screenDensity} dp.",
            bottomMarginPx == marginPx
        )
    }

    @Test
    fun `Проверка_размера_иконки_для_кнопки_НАСТРОЙКИ`() {
        val buttonText = "Настройки"
        val expectedIconSizeDp = 24

        onView(withTextCaseInsensitive(buttonText))
            .check(matches(hasDrawableSize(expectedIconSizeDp)))
    }

    // Проверка запуска Активити для нажатий на кнопки

    @Test
    fun `Проверка_запуска_Активити_для_ПОИСК`() {
        checkActivityLaunch("Поиск")
    }

    @Test
    fun `Проверка_запуска_Активити_для_МЕДИАТЕКА`() {
        checkActivityLaunch("Медиатека")
    }

    @Test
    fun `Проверка_запуска_Активити_для_НАСТРОЙКИ`() {
        checkActivityLaunch("Настройки")
    }

    fun checkActivityLaunch(buttonText: String) {
        val packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName
        onView(withTextCaseInsensitive(buttonText)).perform(click())
        intended(toPackage(packageName))
    }

    fun checkSideMargins(buttonText: String, margin: Int, onlyLeft: Boolean = false, onlyRight: Boolean = false) {

        // Create a Rect to store the button's bounds
        val buttonBounds = Rect()

        // Get the button's coordinates on the screen
        onView(withTextCaseInsensitive(buttonText)).perform(GetViewBoundsAction(buttonBounds))

        // Get the screen dimensions
        val displayMetrics = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val screenDensity = displayMetrics.density

        // Calculate the minimum margin in pixels
        val marginPx = (margin * screenDensity).toInt()

        // Calculate the space on the left and right
        val leftMargin = buttonBounds.left
        val rightMargin = screenWidthPx - buttonBounds.right

        // Assert that both the left and right margins are greater than or equal to the minimum
        if (onlyLeft) {
            assertTrue(
                "Левый отступ некорректный. Ожидается $margin dp, Фактическое расстояние ${leftMargin / screenDensity} dp.",
                leftMargin == marginPx
            )
            return
        }

        if (onlyRight) {
            assertTrue(
                "Правый отступ некорректный. Ожидается $margin dp, Фактическое расстояние ${rightMargin / screenDensity} dp.",
                rightMargin == marginPx
            )
            return
        }

        assertTrue(
            "Левый отступ некорректный. Ожидается $margin dp, Фактическое расстояние ${leftMargin / screenDensity} dp.",
            leftMargin == marginPx
        )
        assertTrue(
            "Правый отступ некорректный. Ожидается $margin dp, Фактическое расстояние ${rightMargin / screenDensity} dp.",
            rightMargin == marginPx
        )

    }

    fun checkVerticalSpaceBetweenViews(
        firstButtonText: String,
        secondButtonText: String,
        requiredSpaceDp: Int
    ) {
        // Create Rect objects to store the bounds
        val firstViewBounds = Rect()
        val secondViewBounds = Rect()

        // Get bounds of the first view
        onView(withTextCaseInsensitive(firstButtonText)).perform(GetViewBoundsAction(firstViewBounds))

        // Get bounds of the second view
        onView(withTextCaseInsensitive(secondButtonText)).perform(GetViewBoundsAction(secondViewBounds))

        // Calculate the screen density
        val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources
        val screenDensity = resources.displayMetrics.density
        val spacePx = (requiredSpaceDp * screenDensity).toInt()

        // Calculate the horizontal and vertical distance
        val verticalDistancePx = Math.abs(firstViewBounds.bottom - secondViewBounds.top)
        val verticalDistanceDp = (verticalDistancePx.toFloat() / screenDensity).toInt()

        // Assert that the distance meets the minimum requirement
        assertTrue(
            "Пространство между кнопками $firstButtonText и $secondButtonText некорректное. " +
                    "Ожидается: $requiredSpaceDp dp. " +
                    "Фактическое расстояние: $verticalDistanceDp dp (вертикаль).",
            verticalDistancePx == spacePx
        )
    }

    fun isWellFormattedText(buttonName: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("Проверка на правильное форматирование текста кнопки $buttonName")
            }

            override fun describeMismatchSafely(item: View?, mismatchDescription: Description?) {
                mismatchDescription?.appendText("Текст кнопки $buttonName не соответствует требуемому формату")
            }

            override fun matchesSafely(item: View?): Boolean {
                val text = (item as? TextView)?.text?.toString() ?: return false

                val firstChar = text.first()
                if (!firstChar.isUpperCase()) {
                    return false
                }

                val remainingText = text.substring(1)
                if (remainingText.isNotEmpty() && !remainingText.all { it.isLowerCase() }) {
                    return false
                }

                // Все условия выполнены
                return true
            }
        }
    }

    fun withTextCaseInsensitive(expectedText: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("С текстом (case-insensitive): $expectedText")
            }

            override fun matchesSafely(item: View): Boolean {
                return (item as? TextView)?.text?.toString()?.equals(expectedText, ignoreCase = true) ?: false
            }
        }
    }

    fun isAbove(targetViewMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Выше указанного View: ")
                targetViewMatcher.describeTo(description)
            }

            override fun matchesSafely(item: View): Boolean {
                val parent = item.parent as? ViewGroup ?: return false
                val targetView: View? = findTargetViewInParent(parent, targetViewMatcher)

                if (targetView == null) return false

                val itemRect = Rect()
                item.getGlobalVisibleRect(itemRect)

                val targetRect = Rect()
                targetView.getGlobalVisibleRect(targetRect)

                // Check if the element is directly above the target
                return itemRect.bottom <= targetRect.top
            }
        }
    }

    private fun findTargetViewInParent(parent: ViewGroup, targetViewMatcher: Matcher<View>): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (targetViewMatcher.matches(child)) {
                return child
            }
        }
        return null
    }

    fun hasChildWithBackgroundColor(expectedColor: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Проверка на правильный цвет фона  #%06X".format(expectedColor and 0xFFFFFF))
            }

            override fun describeMismatchSafely(item: View?, mismatchDescription: Description?) {
                mismatchDescription?.appendText(
                    "Не удалось найти View с фоном цвета #%06X - ВАЖНО: Проверка была только для самого первого (внешнего) ViewGroup".format(
                        expectedColor and 0xFFFFFF
                    )
                )
            }

            override fun matchesSafely(item: View): Boolean {
                if (item !is ViewGroup) {
                    return false
                }

                // Iterate through all children of the view
                for (i in 0 until item.childCount) {
                    val child = item.getChildAt(i)
                    val background = child.background
                    if (background is ColorDrawable && background.color == expectedColor) {
                        return true
                    }
                }
                return false
            }
        }
    }

    fun hasDrawableSize(expectedSizeDp: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Проверка что иконка на кнопке имеет размер  ${expectedSizeDp}dp")
            }

            override fun describeMismatchSafely(item: View?, mismatchDescription: Description?) {
                mismatchDescription?.appendText("Размер иконки на кнопке не соответствует ${expectedSizeDp}dp")
            }

            override fun matchesSafely(item: View): Boolean {
                val textView = item as? TextView ?: return false
                val drawables = textView.compoundDrawables

                val density = InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
                val expectedSizePx = (expectedSizeDp * density).toInt()

                for (drawable in drawables) {
                    if (drawable != null && drawable.intrinsicWidth == expectedSizePx && drawable.intrinsicHeight == expectedSizePx) {
                        return true
                    }
                }
                return false
            }
        }
    }

}

class GetViewBoundsAction(private val outRect: Rect) : ViewAction {

    override fun getConstraints(): Matcher<View> {
        // Correctly specify the type parameter for the Matcher
        return Matchers.allOf(ViewMatchers.isDisplayed(), Matchers.instanceOf(View::class.java))
    }

    override fun getDescription(): String {
        return "getting view bounds"
    }

    override fun perform(uiController: UiController?, view: View?) {
        Log.e("UiTestsSprint8", "GetViewBoundsAction.")
        view?.getGlobalVisibleRect(outRect)
    }
}

