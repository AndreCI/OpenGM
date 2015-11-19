package ch.epfl.sweng.opengm.polls.participants;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.parse.PFMember;

public class Participant {

    private final List<PFMember> participants;
    private final String prefix;
    private final boolean isGroup;
    private final String info;
    private final String name;

    public Participant(PFMember member) {
        this.participants = new ArrayList<>();
        this.participants.add(member);
        this.isGroup = false;
        this.prefix = member.getFirstname().substring(1, 1).concat(member.getLastname().substring(1, 1)).toUpperCase();
        this.info = member.getUsername();
        this.name = member.toString();
    }

    private Participant(String name, List<PFMember> members) {
        participants = new ArrayList<>(members);
        isGroup = true;
        this.prefix = name.substring(1, 2).toUpperCase();
        this.name = name;
        this.info = members.size() + "members";
    }

    public List<PFMember> getParticipants() {
        return participants;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }
}
