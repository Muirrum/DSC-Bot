import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.AuthorInfo;
import net.dv8tion.jda.api.entities.MessageEmbed.Footer;
import net.dv8tion.jda.api.entities.MessageEmbed.Thumbnail;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class BanBuddy implements EventListener{
	@Override
	public void onEvent(GenericEvent event)
	{
		if(event instanceof GuildBanEvent) {
			GuildBanEvent ban = (GuildBanEvent) event;
			System.out.println("BAN: "+ban.getGuild()+" "+ban.getUser().getId());
			Start.addBan(ban.getUser().getId(),ban.getGuild().retrieveBanById(ban.getUser().getId()).complete().getReason());
			try {
				Start.getBans();
			} catch (IOException e) {
				e.printStackTrace();
			}

			TextChannel DSC = Start.jda.getTextChannelById("646545388576178178"); //For use in production.
			//TextChannel DSC = jda.getTextChannelById("668964814684422184"); //For use in testing.
			MessageEmbed embed = new MessageEmbed(null, "Ban!", "Server: "+ban.getGuild().getName()+"\nName: "+ban.getUser().getAsTag()+"\nID: "+ban.getUser().getId(), null, OffsetDateTime.now(), 0xF40C0C, new Thumbnail(ban.getUser().getEffectiveAvatarUrl(), null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null, null);
			DSC.sendMessage(embed).queue();
		} else if(event instanceof GuildMemberJoinEvent) {
			GuildMemberJoinEvent join = (GuildMemberJoinEvent) event;
			System.out.println("JOIN: "+join.getGuild()+" "+join.getUser().getId());
			for (String temp: Start.BL) {
				if (temp.startsWith(join.getUser().getId())) {
					//TextChannel DSC = Start.jda.getTextChannelById("646540745443901472"); //For use in production.
					//TextChannel DSC = jda.getTextChannelById("668964814684422184"); //For use in testing.
					MessageEmbed embed = new MessageEmbed(null, "Banned User Joined "+join.getGuild().getName(), "Name: "+join.getUser().getAsTag()+"\nID: "+join.getUser().getId()+"\nReason: "+Start.BLR.get(Start.BL.indexOf(join.getUser().getId())), null, OffsetDateTime.now(), 0xF40C0C, new Thumbnail(join.getUser().getEffectiveAvatarUrl(), null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null, null);
					join.getGuild().getSystemChannel().sendMessage(embed).queue();
				}
			}
			for (String temp: Start.AL) {
				if (temp.startsWith(join.getUser().getId())) {
					TextChannel DSC = Start.jda.getTextChannelById("646540745443901472"); //For use in production.
					//TextChannel DSC = jda.getTextChannelById("668964814684422184"); //For use in testing.
					MessageEmbed embed = new MessageEmbed(null, "User With Active Advisory Joined "+join.getGuild().getName(), "Name: "+join.getUser().getAsTag()+"\nID: "+join.getUser().getId()+"\nReason: "+Start.ALR.get(Start.AL.indexOf(join.getUser().getId())), null, OffsetDateTime.now(), 0xF40C0C, new Thumbnail(join.getUser().getEffectiveAvatarUrl(), null, 128, 128), null, new AuthorInfo("", null, "", null), null, new Footer("DSC Bot | Powered By Tfinnm Development", "https://cdn.discordapp.com/attachments/646540745443901472/668954814285217792/1920px-Boy_Scouts_of_the_United_Nations.png", null), null, null);
					DSC.sendMessage(embed).queue();
				}
			}
			boolean ageVerified = false;
			for (String temp: Start.AgeL) {
				if (temp.startsWith(join.getUser().getId())) {
					ageVerified = true;
				}
			}
			if (!ageVerified) {
				if (join.getUser().getId().equals("213319973756600322")) {
					if (!Start.msged.contains(join.getUser().getId())) {
						Start.msged.add(join.getUser().getId());
						Start.addUser(join.getUser().getId());
						PrivateChannel pc = join.getUser().openPrivateChannel().complete();
						Message msg = pc.sendMessage("Howdy! You just joined a DSC member server (or already did and never received this message). DSC is a network of scouting servers which work together to provide a better and safer expirience for their members. As part of this mission, member servers enforce BSA's Youth Protection Guidelines, and to assist in doing so, many servers allow members to self identify as over or under 18. In an attempt to consodidate this, you may do this here by selecting - for under 18 or + for over 18.").complete();
						pc.addReactionById(msg.getId(), "➕").queue();
						pc.addReactionById(msg.getId(), "➖").queue();
						pc.pinMessageById(msg.getId());
					}
				}
			}
		} else if (event instanceof MessageReactionAddEvent) {
			MessageReactionAddEvent react = (MessageReactionAddEvent) event;
			if (react.getChannel().getType().equals(ChannelType.PRIVATE)) {
				if (!react.getUser().isBot()) {
					String id = react.getMember().getId();
					if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("➕")) {
						Start.setAge(id, "+");
					}
					if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("➖")) {
						Start.setAge(id, "-");
					}
				}
			}
		}
	}
}
