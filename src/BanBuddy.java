import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class BanBuddy implements EventListener{
	@Override
	public void onEvent(GenericEvent event)
	{
		//System.out.print(event.toString());
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
						Message msg = pc.sendMessage("Howdy! You just joined a DSC member server (or already did and never received this message). DSC is a network of scouting servers which work together to provide a better and safer expirience for their members. As part of this mission, member servers enforce BSA's Youth Protection Guidelines, and to assist in doing so, many servers allow members to self identify as over or under 18. In an attempt to consodidate this, you may do this here by selecting - for under 18 or + for over 18.\nIf you don't receive a confirmation after selecting one, please type `\\\\age`").complete();
						pc.addReactionById(msg.getId(), "➕").queue();
						pc.addReactionById(msg.getId(), "➖").queue();
						pc.pinMessageById(msg.getId()).queue();
					}
				}
			}
		} else if (event instanceof PrivateMessageReceivedEvent) {
			PrivateMessageReceivedEvent post = (PrivateMessageReceivedEvent) event;
			//if (!Start.blocked.contains(post.getAuthor().getId())) {
				List<Attachment> imgs = post.getMessage().getAttachments();
				if (imgs.size() > 0) {
					for (Attachment img: imgs) {
						TextChannel DSC = Start.jda.getTextChannelById("684577265425973285"); //For use in production.
						//TextChannel DSC = jda.getTextChannelById("668964814684422184"); //For use in testing.
						try {
							Message msg = DSC.sendMessage(post.getAuthor().getId()).addFile(img.downloadToFile().get()).complete();
							DSC.addReactionById(msg.getId(), "🦅").queue(); //Eagle
							DSC.addReactionById(msg.getId(), "⛰").queue(); //Summit/Silver
							DSC.addReactionById(msg.getId(), "🏕").queue(); //Camp Staff
							DSC.addReactionById(msg.getId(), "🛂").queue(); //YPT
							DSC.addReactionById(msg.getId(), "↗").queue(); //OA ordeal
							DSC.addReactionById(msg.getId(), "🟥").queue(); //OA brotherhood
							DSC.addReactionById(msg.getId(), "🔺").queue(); //OA vigil
							DSC.addReactionById(msg.getId(), "❌").queue(); //close ticket, Do not move past here
							DSC.addReactionById(msg.getId(), "⚠").queue(); //issue warning
							DSC.addReactionById(msg.getId(), "⛔").queue(); //block from eVerify
						} catch (InterruptedException | ExecutionException e) {
						}
						post.getChannel().sendMessage("[Verify] Opened Request.");
					}
				}
			//}
		} else if (event instanceof PrivateMessageReactionAddEvent) {
			PrivateMessageReactionAddEvent react = (PrivateMessageReactionAddEvent) event;
			System.out.print("react");
			if (!react.getUser().isBot()) {
				String id = react.getChannel().getUser().getId();
				System.out.print(react.getReactionEmote().isEmoji());
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("➕")) {
					Start.setAge(id, "+");
					System.out.print(Start.AgeL);
					react.getChannel().sendMessage("Set age as over 18.").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("➖")) {
					Start.setAge(id, "-");
					System.out.print(Start.AgeL);
					react.getChannel().sendMessage("Set age as under 18.").queue();
				}
			}
		} else if (event instanceof GuildMessageReactionAddEvent) {
			GuildMessageReactionAddEvent react = (GuildMessageReactionAddEvent) event;
			if (react.getChannel().getId().equals("684577265425973285"))
				System.out.print("reactDSC");
			if (!react.getUser().isBot()) {
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("🦅")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					Start.verify(usrid, "eagle.DSC", new ArrayList<String>());
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					pc.sendMessage("[Verify] Granted \"Eagle\".").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("⛰")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					Start.verify(usrid, "summit.DSC", new ArrayList<String>());
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					pc.sendMessage("[Verify] Granted \"Summit\".").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("🏕")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					Start.verify(usrid, "Camp Staff.DSC", new ArrayList<String>());
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					pc.sendMessage("[Verify] Granted \"Camp Staff\".").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("🛂")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					Start.verify(usrid, "YPT.DSC", new ArrayList<String>());
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					pc.sendMessage("[Verify] Granted \"YPT\".").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("↗")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					Start.verify(usrid, "ordeal.DSC", new ArrayList<String>());
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					pc.sendMessage("[Verify] Granted \"Ordeal\".").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("🟥")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					Start.verify(usrid, "brotherhood.DSC", new ArrayList<String>());
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					pc.sendMessage("[Verify] Granted \"Brotherhood\".").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("🔺")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					Start.verify(usrid, "vigil.DSC", new ArrayList<String>());
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					pc.sendMessage("[Verify] Granted \"Vigil\".").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("❌")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					react.getChannel().deleteMessageById(react.getMessageId()).queue();
					pc.sendMessage("[Verify] Request Closed.").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("⚠") && react.getUserId().equals("213319973756600322")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					react.getChannel().deleteMessageById(react.getMessageId()).queue();
					pc.sendMessage("[Verify] ⚠ Your request was flagged by a moderator.").queue();
				}
				if (react.getReactionEmote().isEmoji() && react.getReactionEmote().getEmoji().equals("⛔") && react.getUserId().equals("213319973756600322")) {
					String usrid = react.getChannel().retrieveMessageById(react.getMessageId()).complete().getContentDisplay();
					Start.verify(usrid, "block.DSC", Start.blocked);
					Start.addAdvisory(usrid, "Abuse of the verification system.");
					Start.AL.add(usrid);
					Start.ALR.add("Abuse of the verification system.");
					PrivateChannel pc = Start.jda.getUserById(usrid).openPrivateChannel().complete();
					react.getChannel().deleteMessageById(react.getMessageId()).queue();
					pc.sendMessage("[Verify] ⚠ Your account has been suspended from the verification system.\n\n**Server moderators cannot asist you with this.**\nIf you beleive this was in error, please ask a mod to direct you to a verification system moderator.").queue();
				}
			}
		}
	}
}
