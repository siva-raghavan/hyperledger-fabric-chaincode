package org.hyperledger.fabric.samples.fabcar;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.ContractRuntimeException;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Contract(
        name = "InventoryManagement",
        info = @Info(
                title = "Inventory Management Contract",
                description = "A contract to manage item inventory and refill request status",
                version = "0.0.2-SNAPSHOT",
                license = @License(
                        name = "Apache-2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                ),
                contact = @Contact(
                        email = "contact@example.com",
                        name = "Support",
                        url = "https://www.hyperledger.com/"
                )
        )
)
@Default
public final class InventoryManagement implements ContractInterface {

    private final Genson genson = new Genson();
    private final String INVENTORY_KEY_PREFIX = "inventory:";
    private final String THRESHOLD_KEY_PREFIX = "threshold:";
    private final String REPLEN_STATE_KEY_PREFIX = "replen:";

    private static byte[] toBytes(Object object) {
        return object.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String fromBytes(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Transaction
    public void initializeItemWithInventoryAndThreshold(Context ctx, String itemKey, String inventory, String threshold) {
        ChaincodeStub stub = ctx.getStub();
        stub.putState(INVENTORY_KEY_PREFIX + itemKey, toBytes(Integer.valueOf(inventory)));
        stub.putState(THRESHOLD_KEY_PREFIX + itemKey, toBytes(Integer.valueOf(threshold)));
    }

    @Transaction
    public Integer recordSales(Context ctx, String itemKey, String soldInventoryCount) {
        ChaincodeStub stub = ctx.getStub();
        byte[] inventoryBytes = stub.getState(INVENTORY_KEY_PREFIX + itemKey);
        if (inventoryBytes == null) {
            throw new ContractRuntimeException("Item not found: " + itemKey);
        }
        Integer oldInventory = Integer.valueOf(fromBytes(inventoryBytes));
        Integer newInventory = oldInventory - Integer.valueOf(soldInventoryCount);
        stub.putState(INVENTORY_KEY_PREFIX + itemKey, toBytes(newInventory));
        List<String> args = Arrays.asList("getThresholdForAKey", itemKey);
//        Response res  = stub.invokeChaincodeWithStringArgs("fabcar", args, "mychannel");
//        byte[] getThresholdForKeyInBytes = res.getPayload();
//        if (getThresholdForKeyInBytes == null) {
//            throw new ContractRuntimeException("Item threshold not found: " + itemKey);
//        }
//        Integer threshold = Integer.valueOf(fromBytes(getThresholdForKeyInBytes));
//        if(newInventory < threshold) {
//            stub.invokeChaincodeWithStringArgs("fabcar", "getThresholdForAKey", itemKey);
//        }
        Integer threshold = getThresholdForAKey(ctx, itemKey);
        return threshold;
    }

    @Transaction
    public Integer getThresholdForAKey(Context ctx, String itemKey) {
        ChaincodeStub stub = ctx.getStub();
        byte[] thresholdBytes = stub.getState(THRESHOLD_KEY_PREFIX + itemKey);
        if (thresholdBytes == null) {
            throw new ContractRuntimeException("Item threshold not defined: " + itemKey);
        }
        Integer threshold = Integer.valueOf(fromBytes(thresholdBytes));
        return threshold;
    }

    @Transaction
    public void requestReplen(Context ctx, String itemKey) {
        ChaincodeStub stub = ctx.getStub();
        stub.putState(REPLEN_STATE_KEY_PREFIX + itemKey, toBytes("REFILL_REQUEST"));
    }

    @Transaction
    public String getInventoryHistory(Context ctx, String itemKey) {
        List<String> history = new ArrayList<>();
        QueryResultsIterator<KeyModification> results = ctx.getStub().getHistoryForKey(INVENTORY_KEY_PREFIX + itemKey);
        if (results == null) {
            return "";
        }
        Iterator<KeyModification> iterator = results.iterator();
        while (iterator.hasNext()) {
            history.add(iterator.next().getStringValue());
        }
        return genson.serialize(history);
    }

    @Transaction
    public QueryResultsIterator<KeyModification> getThresholdHistory(Context ctx, String itemKey) {
        return ctx.getStub().getHistoryForKey(THRESHOLD_KEY_PREFIX + itemKey);
    }

    @Transaction
    public QueryResultsIterator<KeyModification> getReplenStateHistory(Context ctx, String itemKey) {
        return ctx.getStub().getHistoryForKey(REPLEN_STATE_KEY_PREFIX + itemKey);
    }
}

