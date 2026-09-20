public class ValidateShoppingCart {

    private static class CatalogItem {
        String itemId;
        long available;
        long minLimit;
        long maxLimit;
    }

    private static class CartLine {
        String itemId;
        long quantity;
    }

    private static class InputData {
        List<CatalogEntry> catalogEntries;
        List<CartLine> cartLines;
    }

    private static class CartSummary {
        long totalQuantity;
        boolean invalidQuantity;
    }

    public static Map<String, CatalogItem> loadCatalog(
        List<CatalogItem> items) {
            Map<String, CatalogItem> catalog = new HashMap<>();

            for(CatalogItem item : items) {
                catalog.put(item.getItemId(), item);
            }

            return catalog;
        }
    }

    public static Map<String, List<String>> validateCartItems(Map<String, CatalogItem> catalog, Map<String, CartSummary> cart) {
        Map<String, List<String>> invalidItems = new TreeMap<>();

        for(Map.Entry<String, CartSummary> entry : cart.entrySet()) {
            String itemId = entry.getKey();
            CartSummary summary = entry.getValue();

            CatalogItem item = catalog.get(itemId);

            List<String> errors = new ArrayList<>();

            if(item == null) {
                errors.add("UNKNOWN_ITEM");
            }

            if(summary.invalidQuantity) {
                errors.add("INVALID_QUANTITY");
            }

            if(item != nulll && !summary.invalidQuantity) {
                long total = summary.totalQuantity;

                if(total < item.getMinLimit()) {
                    errors.add("BELOW_MIN");
                }

                if(total > item.getMaxLimit) {
                    errors.add("ABOVE_MAX");
                }

                if(total > item.getAvailable()) {
                    errors.add("OUT_OF_STOCK");
                }

                if(!errors.isEmpty()) {
                    invalidItems.put(itemId, errors);
                }
            }
        }

        return invalidItems;
    }
}
