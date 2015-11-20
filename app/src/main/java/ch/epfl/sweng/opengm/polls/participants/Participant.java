package ch.epfl.sweng.opengm.polls.participants;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.parse.PFMember;

public class Participant implements Comparable<Participant> {

    private final List<PFMember> participants;
    private final String prefix;
    private final boolean isGroup;
    private final String info;
    private final String name;

    public Participant(PFMember member) {
        this.participants = new ArrayList<>();
        this.participants.add(member);
        this.isGroup = false;
        this.prefix = member.getFirstname().substring(0, 1).concat(member.getLastname().substring(0, 1)).toUpperCase();
        this.info = member.getUsername();
        this.name = member.getDisplayedName();
    }

    public Participant(String name, List<PFMember> members) {
        participants = new ArrayList<>(members);
        isGroup = true;
        this.prefix = name.substring(0, 2).toUpperCase();
        this.name = name;
        this.info = members.size() + " member" + (members.size() > 1 ? "s" : "");
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

    @Override
    public String toString() {
        return name.concat(info);
    }

    @Override
    public int compareTo(Participant another) {
        if (isGroup && !another.isGroup) {
            return -1;
        } else if (!isGroup && another.isGroup) {
            return 1;
        } else {
            return prefix.compareTo(another.prefix);
        }
    }
}
