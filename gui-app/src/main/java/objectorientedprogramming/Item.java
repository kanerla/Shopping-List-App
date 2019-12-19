package objectorientedprogramming;

/**
 * An item in the shopping list.
 *
 * @author Laura Kanerva.
 */
public class Item {
    private int amount;
    private String item;

    /**
     * Empty class constructor
     */
    public Item() {

    }

    /**
     * Class constructor.
     * 
     * @param amount amount of items declared in the list
     * @param item name of the item added to the list
     */
    public Item(int amount, String item) {
        this.amount = amount;
        this.item = item;
    }

    /**
     * Returns the value of amount variable.
     * 
     * @return amount of items
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets value for amount variable.
     * 
     * @param a amount of items
     */
    public void setAmount(int a) {
        this.amount = a;
    }

    /**
     * Returns the name of the item.
     * 
     * @return item to be returned
     */
    public String getItem() {
        return item;
    }

    /**
     * Sets the item.
     * 
     * @param i item on the list
     */
    public void setItem(String i) {
        this.item = i;
    }
}