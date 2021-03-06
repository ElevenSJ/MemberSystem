package com.yuntongxun.ecdemo.ui.chatting.model;

import com.lyp.membersystem.R;
import com.yuntongxun.ecdemo.ui.chatting.holder.BaseHolder;
import com.yuntongxun.ecdemo.ui.chatting.holder.RedPacketAckViewHolder;
import com.yuntongxun.ecdemo.ui.chatting.view.ChattingItemContainer;
import com.yuntongxun.ecsdk.ECMessage;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by ustc on 2016/6/24.
 */
public class RedPacketAckTxRow extends BaseChattingRow {

    public RedPacketAckTxRow(int type) {
        super(type);
    }

    /* (non-Javadoc)
     * @see com.hisun.cas.model.im.ChattingRow#buildChatView(android.view.LayoutInflater, android.view.View)
     */
    @Override
    public View buildChatView(LayoutInflater inflater, View convertView) {
        //we have a don't have a converView so we'll have to create a new one
        if (convertView == null || ((BaseHolder) convertView.getTag()).getType() != mRowType) {
            convertView = new ChattingItemContainer(inflater, R.layout.chatting_item_redpacket_ack_to);
            //use the view holder pattern to save of already looked up subviews
            RedPacketAckViewHolder holder = new RedPacketAckViewHolder(mRowType);
            convertView.setTag(holder.initBaseHolder(convertView, false));
        }
        return convertView;
    }

    @Override
    public void buildChattingData(Context context, BaseHolder baseHolder,
                                  ECMessage msg, int position) {
        RedPacketAckViewHolder holder = (RedPacketAckViewHolder) baseHolder;
        holder.getChattingTime().setVisibility(View.GONE);
        ECMessage message = msg;
        if (message != null) {
            if (message.getType() == ECMessage.Type.TXT) {
//                JSONObject jsonObject = CheckRedPacketMessageUtil.isRedPacketAckMessage(message);
//                if (jsonObject != null) {
//                    holder.getChattingAvatar().setVisibility(View.GONE);
//                    holder.getChattingUser().setVisibility(View.GONE);
//                    holder.getChattingTime().setVisibility(View.GONE);
//
//                    String currentUserId = CCPAppManager.getClientUser().getUserId();   //当前登陆用户id
//                    String recieveUserId = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_ID);//红包接收者id
//                    String recieveUserNick = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_RECEIVER_NAME);//红包接收者昵称
//                    String sendUserId = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_SENDER_ID);//红包发送者id
//                    String sendUserNick = jsonObject.getString(RedPacketConstant.EXTRA_RED_PACKET_SENDER_NAME);//红包发送者昵称
//                    String text = "";
//                    //发送者和领取者都是自己-
//                    if (currentUserId.equals(recieveUserId) && currentUserId.equals(sendUserId)) {
//                        text = context.getResources().getString(R.string.money_msg_take_money);
//                        holder.getRedPacketAckMsgTv().setVisibility(View.VISIBLE);
//                    } else if (currentUserId.equals(sendUserId)) {
//                        //我仅仅是发送者
//                        text = String.format(context.getResources().getString(R.string.money_msg_someone_take_money), recieveUserNick);
//                        holder.getRedPacketAckMsgTv().setVisibility(View.VISIBLE);
//                    } else if (currentUserId.equals(recieveUserId)) {
//                        //我仅仅是接收者
//                        text = String.format(context.getResources().getString(R.string.money_msg_take_someone_money), sendUserNick);
//                        holder.getRedPacketAckMsgTv().setVisibility(View.VISIBLE);
//                    }
//                    holder.getRedPacketAckMsgTv().setText(text);
//                }
            }
        }
    }


    @Override
    public int getChatViewType() {
        return ChattingRowType.REDPACKE_ROW_ACK_TO.ordinal();
    }

    @Override
    public boolean onCreateRowContextMenu(ContextMenu contextMenu,
                                          View targetView, ECMessage detail) {

        return false;
    }


}
