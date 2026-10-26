export interface AccommodationDTO {
    id: Key | null | undefined;
    name: string | undefined;
    address: string | undefined;
    type: string | undefined;
    checkInDate: string | undefined;
    checkOutDate: string | undefined;
    review: string | undefined;
    cost: number | undefined;
}