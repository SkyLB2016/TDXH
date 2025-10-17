package com.sky.oa.adapter.itemtouch;//package com.sky.oa.adapter;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.RecyclerView;
//
///**
// * @Description:
// * @Author: 李彬
// * @CreateDate: 2022/4/13 4:04 下午
// * @Version: 1.0
// */
//public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
//    private ItemTouchHelperListener listener;
//
//    public ItemTouchHelperCallback(ItemTouchHelperListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//        int swipe = 0;
//        return makeMovementFlags(dragFlags, swipe);
//    }
//
//    @Override
//    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//        listener.onItemMove(viewHolder.getLayoutPosition(), target.getLayoutPosition());
//        return false;
//    }
//
//    @Override
//    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//    }
//
//    @Override
//    public boolean isItemViewSwipeEnabled() {
//        return false;
//    }
//}
