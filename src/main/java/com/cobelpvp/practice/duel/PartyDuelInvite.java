package com.cobelpvp.practice.duel;

import com.cobelpvp.practice.party.Party;
import com.cobelpvp.practice.kittype.KitType;

public final class PartyDuelInvite extends DuelInvite<Party> {

    public PartyDuelInvite(Party sender, Party target, KitType kitTypes) {
        super(sender, target, kitTypes, null);
    }

}