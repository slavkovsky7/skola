%% Data
clear all
close all

gama=0.1

% rewards
world.rewardStep = -0.03;
world.rewardGoodGoal = 1;
world.rewardBadGoal = -1;

world.blank = [2 2]; % unusable grid state
world.goal1 = [1 4]; % "good" exit state
world.goal2 = [2 4]; % "bad exit state

world.R = ones(3, 4) * world.rewardStep;
world.R(world.blank(1), world.blank(2)) = 0;
world.R(world.goal1(1), world.goal1(2)) = world.rewardGoodGoal;
world.R(world.goal2(1), world.goal2(2)) = world.rewardBadGoal;

world.probs = [0.8 0.1 0 0.1]; %prob[on righ back left];

world.actions = [ -1, 0 % up
            0, 1 % right
            1, 0 % down
            0, -1]; % left


V = {};
V{1} = [0,0,0,0] 


function [max, index ] = v(state)
	for ia=1:size(world.actions,1)
		Rs = world.R(state);

		s_ = [size()]
		for ip=1:size(world.probs, 1)
			new_state = next_state(world,state,action);
			new_state
		end
	end
end


for t = 1 : 100
	size(world.R,1)
	V{end+1} = zeros(size(world.R));


	for sx=1:size(world.R,2)
		for sy=1:size(world.R,1)
			m,index = v( [sx,sy] );
		end
	end 
	%for a=1:size(world.actions,1)
	%end
	quit

end
            
% you can use -> next_state(world,state,action)






