function [pos] = next_state(world, position, action)

pos = position + world.actions(action,:);

if (prod(pos == world.blank))|| (sum(pos < 1)) || (pos(1) >3) || (pos(2) > 4)
    pos = position;
end
