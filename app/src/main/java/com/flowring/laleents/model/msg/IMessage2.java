package com.flowring.laleents.model.msg;

import java.util.Date;

public interface IMessage2 {

    /**
     * Returns message identifier
     *
     * @return the message id
     */
    String getId();

    /**
     * Returns message text
     *
     * @return the message text
     */
    String getText();

    String getContent();


    /**
     * Returns message author.
     *
     * @return the message author
     */
    String getUserID();

    /**
     * Returns message creation date
     *
     * @return the message creation date
     */
    Date getCreatedAt();


    String getMessageType();

    boolean isRetract();

    boolean isSelect();

    void setSelect(boolean isSelect);

    /**
     *
     * 誰踢出
     * */

    boolean isAdm();
}
