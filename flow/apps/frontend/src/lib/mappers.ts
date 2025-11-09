// Function to map an array of EntityValueDefinition to FilterOption
import { EntityValueDefinition } from "@/types/commons";
import { FilterOption } from "@/types/ui-components";

export const mapEntityToFilterOptions = (
  entities: EntityValueDefinition[],
): FilterOption[] => {
  return entities.map((entity) => ({
    label: entity.value,
    value: entity.value, // Map value directly
    icon: undefined, // Optional, you can add logic to map this if needed
  }));
};
